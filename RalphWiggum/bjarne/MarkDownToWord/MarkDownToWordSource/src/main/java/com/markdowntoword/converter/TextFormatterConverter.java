package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Converter for adding Markdown-style text formatting to Word documents.
 * Converts bold, italic, and bold-italic Markdown formatting to equivalent Word formatting.
 */
public class TextFormatterConverter {

    private final XWPFDocument document;

    /**
     * Creates a TextFormatterConverter that will add formatted text to the specified document.
     *
     * @param document the XWPFDocument to add formatted text to
     * @throws IllegalArgumentException if document is null
     */
    public TextFormatterConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a paragraph with bold text content to the document.
     *
     * @param text the text content for the bold paragraph
     * @throws IllegalArgumentException if text is null
     */
    public void addBoldText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Bold text cannot be null");
        }
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
    }

    /**
     * Converts a Flexmark StrongEmphasis AST node to a Word paragraph with bold text.
     * This method extracts the text content from the AST node
     * and converts it to a Word paragraph with bold formatting.
     *
     * @param strongEmphasis the Flexmark StrongEmphasis AST node to convert
     * @throws IllegalArgumentException if strongEmphasis is null
     */
    public void convertStrongEmphasis(StrongEmphasis strongEmphasis) {
        if (strongEmphasis == null) {
            throw new IllegalArgumentException("StrongEmphasis cannot be null");
        }

        String text = getTextFromNode(strongEmphasis);
        addBoldText(text);
    }

    /**
     * Adds a paragraph with italic text content to the document.
     *
     * @param text the text content for the italic paragraph
     * @throws IllegalArgumentException if text is null
     */
    public void addItalicText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Italic text cannot be null");
        }
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setItalic(true);
    }

    /**
     * Converts a Flexmark Emphasis AST node to a Word paragraph with italic text.
     * This method extracts the text content from the AST node
     * and converts it to a Word paragraph with italic formatting.
     *
     * @param emphasis the Flexmark Emphasis AST node to convert
     * @throws IllegalArgumentException if emphasis is null
     */
    public void convertEmphasis(Emphasis emphasis) {
        if (emphasis == null) {
            throw new IllegalArgumentException("Emphasis cannot be null");
        }

        String text = getTextFromNode(emphasis);
        addItalicText(text);
    }

    /**
     * Adds a paragraph with bold and italic text content to the document.
     *
     * @param text the text content for the bold-italic paragraph
     * @throws IllegalArgumentException if text is null
     */
    public void addBoldItalicText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Bold-italic text cannot be null");
        }
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setItalic(true);
    }

    /**
     * Converts a Flexmark Document AST to Word paragraphs with text formatting.
     * This method iterates through the document's children and converts
     * all StrongEmphasis and Emphasis nodes to Word paragraphs with appropriate formatting.
     * Handles nested formatting such as **bold *and* italic**.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(StrongEmphasis.class, this::convertStrongEmphasis),
            new VisitHandler<>(Emphasis.class, this::convertEmphasis)
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
