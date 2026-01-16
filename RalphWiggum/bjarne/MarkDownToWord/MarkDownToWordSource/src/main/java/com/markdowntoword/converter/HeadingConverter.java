package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Converter for adding Markdown-style headings to Word documents.
 * Converts heading levels 1-6 to Word paragraph styles Heading1 through Heading6.
 */
public class HeadingConverter {

    private static final int MIN_HEADING_LEVEL = 1;
    private static final int MAX_HEADING_LEVEL = 6;

    private final XWPFDocument document;

    /**
     * Creates a HeadingConverter that will add headings to the specified document.
     *
     * @param document the XWPFDocument to add headings to
     * @throws IllegalArgumentException if document is null
     */
    public HeadingConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a heading with the specified text and level to the document.
     * The heading style in Word will be "Heading1" through "Heading6" based on the level.
     *
     * @param text  the heading text content
     * @param level the heading level (1-6, where 1 is the highest level)
     * @throws IllegalArgumentException if text is null
     * @throws IllegalArgumentException if level is less than 1
     * @throws IllegalArgumentException if level is greater than 6
     */
    public void addHeading(String text, int level) {
        if (text == null) {
            throw new IllegalArgumentException("Heading text cannot be null");
        }
        if (level < MIN_HEADING_LEVEL) {
            throw new IllegalArgumentException(
                "Heading level cannot be less than " + MIN_HEADING_LEVEL + ", was: " + level
            );
        }
        if (level > MAX_HEADING_LEVEL) {
            throw new IllegalArgumentException(
                "Heading level cannot be greater than " + MAX_HEADING_LEVEL + ", was: " + level
            );
        }

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setStyle("Heading" + level);
        paragraph.createRun().setText(text);
    }

    /**
     * Converts a Flexmark Heading AST node to a Word heading paragraph.
     * This method extracts the heading level and text from the AST node
     * and converts them to a Word heading with the appropriate style.
     *
     * @param heading the Flexmark Heading AST node to convert
     * @throws IllegalArgumentException if heading is null
     */
    public void convertHeading(Heading heading) {
        if (heading == null) {
            throw new IllegalArgumentException("Heading cannot be null");
        }

        int level = heading.getLevel();
        String text = getTextFromNode(heading);

        addHeading(text, level);
    }

    /**
     * Converts a Flexmark Document AST to Word headings.
     * This method iterates through the document's children and converts
     * all Heading nodes to Word paragraphs with appropriate styles.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(Heading.class, this::convertHeading)
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
     * @param node the node to extract text from
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
