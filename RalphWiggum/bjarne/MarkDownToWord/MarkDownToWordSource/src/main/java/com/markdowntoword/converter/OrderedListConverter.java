package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.OrderedList;
import com.vladsch.flexmark.ast.OrderedListItem;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;

/**
 * Converter for adding Markdown-style ordered lists to Word documents.
 * Converts Flexmark OrderedList AST nodes (items with 1., 2., 3.) to Word paragraphs
 * with decimal numbering using Apache POI's XWPFNumbering.
 */
public class OrderedListConverter {

    private static final int ORDERED_NUM_ID = 2;
    private static final int SINGLE_LEVEL_ILVL = 0;

    private final XWPFDocument document;

    /**
     * Creates an OrderedListConverter that will add numbered lists to the specified document.
     *
     * @param document the XWPFDocument to add numbered lists to
     * @throws IllegalArgumentException if document is null
     */
    public OrderedListConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
        ensureNumberingDefinition();
    }

    /**
     * Adds a numbered list item with the specified text to the document.
     * The item will be formatted with decimal numbering (1, 2, 3, ...).
     *
     * @param text the text content for the numbered item
     * @throws IllegalArgumentException if text is null
     */
    public void addOrderedListItem(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Ordered list item text cannot be null");
        }

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setNumID(BigInteger.valueOf(ORDERED_NUM_ID));
        paragraph.createRun().setText(text);
    }

    /**
     * Converts a Flexmark OrderedList AST node to Word numbered list paragraphs.
     * This method iterates through all list items in the OrderedList node
     * and converts each to a Word paragraph with decimal numbering.
     *
     * @param list the Flexmark OrderedList AST node to convert
     * @throws IllegalArgumentException if list is null
     */
    public void convertOrderedList(OrderedList list) {
        if (list == null) {
            throw new IllegalArgumentException("OrderedList cannot be null");
        }

        Node child = list.getFirstChild();
        while (child != null) {
            if (child instanceof OrderedListItem listItem) {
                String text = getTextFromNode(listItem);
                addOrderedListItem(text);
            }
            child = child.getNext();
        }
    }

    /**
     * Converts a Flexmark Document AST to Word numbered lists.
     * This method iterates through the document's children and converts
     * all OrderedList nodes to Word paragraphs with decimal numbering.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(OrderedList.class, this::convertOrderedList)
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
     * Ensures that the document has a numbering definition for ordered lists.
     * Creates a simple single-level decimal numbering definition if one doesn't exist.
     * This method creates a proper abstract numbering definition with decimal format.
     */
    private void ensureNumberingDefinition() {
        XWPFNumbering numbering = document.getNumbering();
        if (numbering == null) {
            numbering = document.createNumbering();
        }

        // Check if the ordered numbering definition already exists
        if (numbering.getNum(BigInteger.valueOf(ORDERED_NUM_ID)) != null) {
            return;
        }

        // Create an abstract numbering definition with decimal numbering
        CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();

        // Build the decimal numbering level XML using addNewLvl() and set properties
        CTDecimalNumber start = abstractNum.addNewLvl().addNewStart();
        start.setVal(BigInteger.ONE);

        CTNumFmt numFmt = abstractNum.getLvlArray(0).addNewNumFmt();
        numFmt.setVal(STNumberFormat.DECIMAL);

        CTLevelText lvlText = abstractNum.getLvlArray(0).addNewLvlText();
        lvlText.setVal("%1.");

        // Wrap the CTAbstractNum in XWPFAbstractNum
        XWPFAbstractNum xwpfAbstractNum = new XWPFAbstractNum(abstractNum);
        xwpfAbstractNum.setNumbering(numbering);

        // Add the abstract numbering definition and get its ID
        BigInteger abstractNumId = numbering.addAbstractNum(xwpfAbstractNum);

        // Link the abstract numbering to a concrete numbering instance with ID 2
        numbering.addNum(abstractNumId);
    }
}
