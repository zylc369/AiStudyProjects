package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Converter for adding Markdown-style inline code to Word documents.
 * Converts Flexmark Code AST nodes (backtick-enclosed text) to Word paragraphs with monospace font formatting.
 */
public class InlineCodeConverter {

    private static final String MONOSPACE_FONT_FAMILY = "Courier New";

    private final XWPFDocument document;

    /**
     * Creates an InlineCodeConverter that will add inline code to the specified document.
     *
     * @param document the XWPFDocument to add inline code to
     * @throws IllegalArgumentException if document is null
     */
    public InlineCodeConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a paragraph with monospace font text content to the document.
     *
     * @param text the text content for the inline code paragraph
     * @throws IllegalArgumentException if text is null
     */
    public void addInlineCode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Inline code text cannot be null");
        }
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily(MONOSPACE_FONT_FAMILY);
    }

    /**
     * Converts a Flexmark Code AST node to a Word paragraph with monospace text.
     * This method extracts the text content from the AST node
     * and converts it to a Word paragraph with monospace font formatting.
     *
     * @param code the Flexmark Code AST node to convert
     * @throws IllegalArgumentException if code is null
     */
    public void convertCode(Code code) {
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }

        String text = getTextFromNode(code);
        addInlineCode(text);
    }

    /**
     * Converts a Flexmark Document AST to Word paragraphs with inline code formatting.
     * This method iterates through the document's children and converts
     * all Code nodes to Word paragraphs with monospace font formatting.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(Code.class, this::convertCode)
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
