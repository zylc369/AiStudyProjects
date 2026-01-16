package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Converter for adding Markdown-style paragraphs to Word documents.
 * Converts Flexmark Paragraph AST nodes to Word XWPFParagraph elements.
 */
public class ParagraphConverter {

    private final XWPFDocument document;

    /**
     * Creates a ParagraphConverter that will add paragraphs to the specified document.
     *
     * @param document the XWPFDocument to add paragraphs to
     * @throws IllegalArgumentException if document is null
     */
    public ParagraphConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a paragraph with the specified text content to the document.
     *
     * @param text the text content for the paragraph
     * @throws IllegalArgumentException if text is null
     */
    public void addParagraph(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Paragraph text cannot be null");
        }
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().setText(text);
    }

    /**
     * Converts a Flexmark Paragraph AST node to a Word paragraph.
     * This method extracts the text content from the AST node
     * and converts it to a Word paragraph.
     *
     * @param paragraph the Flexmark Paragraph AST node to convert
     * @throws IllegalArgumentException if paragraph is null
     */
    public void convertParagraph(Paragraph paragraph) {
        if (paragraph == null) {
            throw new IllegalArgumentException("Paragraph cannot be null");
        }

        String text = getTextFromNode(paragraph);
        addParagraph(text);
    }

    /**
     * Converts a Flexmark Document AST to Word paragraphs.
     * This method iterates through the document's children and converts
     * all Paragraph nodes to Word paragraphs.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(Paragraph.class, this::convertParagraph)
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
