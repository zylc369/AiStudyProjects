package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Converter for adding Markdown-style links to Word documents.
 * Converts Markdown link syntax [link text](url) to Word hyperlinks.
 */
public class LinkConverter {

    private static final String HYPERLINK_RELATION_ID_PREFIX = "rId";

    private final XWPFDocument document;
    private int hyperlinkCounter = 0;

    /**
     * Creates a LinkConverter that will add links to the specified document.
     *
     * @param document the XWPFDocument to add links to
     * @throws IllegalArgumentException if document is null
     */
    public LinkConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a paragraph with a hyperlink to the document.
     * The hyperlink will have the specified text as display text and will link to the specified URL.
     *
     * @param text the display text for the hyperlink
     * @param url  the target URL for the hyperlink
     * @throws IllegalArgumentException if text is null
     * @throws IllegalArgumentException if url is null
     * @throws IllegalArgumentException if url is blank
     */
    public void addHyperlink(String text, String url) {
        if (text == null) {
            throw new IllegalArgumentException("Hyperlink text cannot be null");
        }
        if (url == null) {
            throw new IllegalArgumentException("Hyperlink URL cannot be null");
        }
        if (url.isBlank()) {
            throw new IllegalArgumentException("Hyperlink URL cannot be blank");
        }

        XWPFParagraph paragraph = document.createParagraph();

        // Increment hyperlink counter for unique ID
        hyperlinkCounter++;
        String relationId = HYPERLINK_RELATION_ID_PREFIX + hyperlinkCounter;

        // Add the hyperlink relationship to the document
        document.getPackagePart().addExternalRelationship(url,
            org.apache.poi.openxml4j.opc.PackageRelationshipTypes.HYPERLINK_PART, relationId);

        // Create the hyperlink run using the paragraph's CTP
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink hyperlink =
            paragraph.getCTP().addNewHyperlink();
        hyperlink.setId(relationId);

        // Create the run within the hyperlink
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR run = hyperlink.addNewR();

        // Set the text in the run's text element
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText ctText = run.addNewT();
        ctText.setStringValue(text);
    }

    /**
     * Converts a Flexmark Link AST node to a Word hyperlink paragraph.
     * This method extracts the link text and URL from the AST node
     * and converts them to a Word hyperlink.
     *
     * @param link the Flexmark Link AST node to convert
     * @throws IllegalArgumentException if link is null
     */
    public void convertLink(Link link) {
        if (link == null) {
            throw new IllegalArgumentException("Link cannot be null");
        }

        String text = getTextFromNode(link);
        String url = link.getUrl().toString();

        addHyperlink(text, url);
    }

    /**
     * Converts a Flexmark Document AST to Word hyperlinks.
     * This method iterates through the document's children and converts
     * all Link nodes to Word hyperlinks.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(Link.class, this::convertLink)
        );

        visitor.visit(document);
    }

    /**
     * Extracts the text content from a Flexmark AST node.
     * This method recursively visits child nodes to collect all text content.
     *
     * @param node the AST node to extract text from
     * @return the concatenated text content of the node and its children
     */
    private String getTextFromNode(Node node) {
        StringBuilder textBuilder = new StringBuilder();
        appendTextFromNode(node, textBuilder);
        return textBuilder.toString();
    }

    /**
     * Recursively appends text from a node and its children to a StringBuilder.
     *
     * @param node    the node to extract text from
     * @param builder the StringBuilder to append text to
     */
    private void appendTextFromNode(Node node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        // Append text content for Text nodes
        if (node instanceof Text textNode) {
            builder.append(textNode.getChars());
        }

        // Process children recursively
        Node child = node.getFirstChild();
        while (child != null) {
            appendTextFromNode(child, builder);
            child = child.getNext();
        }
    }

    /**
     * Returns the underlying XWPFDocument instance.
     * This allows access to advanced Apache POI features if needed.
     *
     * @return the XWPFDocument instance
     */
    protected XWPFDocument getDocument() {
        return document;
    }
}
