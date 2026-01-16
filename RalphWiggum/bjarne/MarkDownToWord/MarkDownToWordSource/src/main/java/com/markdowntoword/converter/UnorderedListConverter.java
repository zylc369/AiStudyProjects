package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.math.BigInteger;

/**
 * Converter for adding Markdown-style unordered lists to Word documents.
 * Converts Flexmark BulletList AST nodes (items with - * +) to Word paragraphs
 * with bullet numbering using Apache POI's XWPFNumbering.
 */
public class UnorderedListConverter {

    private static final String BULLET_NUMBERING_ID = "markdownBullet";
    private static final int SINGLE_LEVEL_NUM_ID = 1;
    private static final int SINGLE_LEVEL_ILVL = 0;

    private final XWPFDocument document;

    /**
     * Creates an UnorderedListConverter that will add bullet lists to the specified document.
     *
     * @param document the XWPFDocument to add bullet lists to
     * @throws IllegalArgumentException if document is null
     */
    public UnorderedListConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
        ensureNumberingDefinition();
    }

    /**
     * Adds a bullet list item with the specified text to the document.
     * The item will be formatted as a bullet point using the default bullet style.
     *
     * @param text the text content for the bullet item
     * @throws IllegalArgumentException if text is null
     */
    public void addBulletListItem(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Bullet item text cannot be null");
        }

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setNumID(BigInteger.valueOf(SINGLE_LEVEL_NUM_ID));
        paragraph.createRun().setText(text);
    }

    /**
     * Converts a Flexmark BulletList AST node to Word bullet list paragraphs.
     * This method iterates through all list items in the BulletList node
     * and converts each to a Word paragraph with bullet numbering.
     *
     * @param list the Flexmark BulletList AST node to convert
     * @throws IllegalArgumentException if list is null
     */
    public void convertBulletList(BulletList list) {
        if (list == null) {
            throw new IllegalArgumentException("BulletList cannot be null");
        }

        Node child = list.getFirstChild();
        while (child != null) {
            if (child instanceof BulletListItem listItem) {
                String text = getTextFromNode(listItem);
                addBulletListItem(text);
            }
            child = child.getNext();
        }
    }

    /**
     * Converts a Flexmark Document AST to Word bullet lists.
     * This method iterates through the document's children and converts
     * all BulletList nodes to Word paragraphs with bullet numbering.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(BulletList.class, this::convertBulletList)
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

    /**
     * Ensures that the document has a numbering definition for bullet lists.
     * Creates a simple single-level bullet numbering definition if one doesn't exist.
     */
    private void ensureNumberingDefinition() {
        XWPFNumbering numbering = document.getNumbering();
        if (numbering == null) {
            numbering = document.createNumbering();
        }

        // Check if the bullet numbering definition already exists
        if (numbering.getNum(BigInteger.valueOf(SINGLE_LEVEL_NUM_ID)) != null) {
            return;
        }

        // Create a new bullet numbering definition
        // The default XWPFNumbering.createBullet() creates a bullet numbering definition
        numbering.addNum(BigInteger.valueOf(SINGLE_LEVEL_NUM_ID));
    }
}
