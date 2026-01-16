package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.BulletListItem;
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
 * Converter for nested Markdown lists to Word multi-level numbered lists.
 * Converts Flexmark BulletList and OrderedList AST nodes with nested children
 * to Word paragraphs with proper indentation levels (ilvl) using Apache POI's XWPFNumbering.
 *
 * Supports up to 4 levels of nesting for both bullet and ordered lists.
 */
public class NestedListConverter {

    private static final int NESTED_BULLET_NUM_ID = 3;
    private static final int NESTED_ORDERED_NUM_ID = 4;
    private static final int MAX_NESTING_LEVEL = 3;

    private final XWPFDocument document;

    /**
     * Creates a NestedListConverter that will add nested lists to the specified document.
     *
     * @param document the XWPFDocument to add nested lists to
     * @throws IllegalArgumentException if document is null
     */
    public NestedListConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
        ensureBulletNumberingDefinition();
        ensureOrderedNumberingDefinition();
    }

    /**
     * Converts a Flexmark Document AST to Word nested lists.
     * This method visits all BulletList and OrderedList nodes and converts them
     * to Word paragraphs with proper multi-level numbering and indentation.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(BulletList.class, this::convertBulletList),
            new VisitHandler<>(OrderedList.class, this::convertOrderedList)
        );

        visitor.visit(document);
    }

    /**
     * Converts a Flexmark BulletList AST node to Word nested bullet list paragraphs.
     * Handles nested bullet lists by recursively processing child BulletList nodes
     * within BulletListItem nodes with proper indentation levels.
     *
     * @param list the Flexmark BulletList AST node to convert
     * @throws IllegalArgumentException if list is null
     */
    public void convertBulletList(BulletList list) {
        if (list == null) {
            throw new IllegalArgumentException("BulletList cannot be null");
        }
        convertBulletList(list, 0);
    }

    /**
     * Converts a Flexmark BulletList AST node at the specified nesting level.
     *
     * @param list the Flexmark BulletList AST node to convert
     * @param level the nesting level (0-3)
     */
    private void convertBulletList(BulletList list, int level) {
        Node child = list.getFirstChild();
        while (child != null) {
            if (child instanceof BulletListItem listItem) {
                convertBulletListItem(listItem, level);
            }
            child = child.getNext();
        }
    }

    /**
     * Converts a Flexmark BulletListItem to a Word paragraph with bullet numbering
     * at the specified nesting level. Recursively processes any nested BulletList nodes.
     *
     * @param listItem the Flexmark BulletListItem to convert
     * @param level the nesting level (0-3)
     */
    private void convertBulletListItem(BulletListItem listItem, int level) {
        String text = getTextFromNode(listItem);
        addBulletListItem(text, level);

        // Check for nested bullet lists within this list item
        Node child = listItem.getFirstChild();
        while (child != null) {
            if (child instanceof BulletList nestedList && level < MAX_NESTING_LEVEL) {
                convertBulletList(nestedList, level + 1);
            }
            child = child.getNext();
        }
    }

    /**
     * Adds a bullet list item with the specified text to the document at the given level.
     * The item will be formatted with bullet numbering appropriate for the level.
     *
     * @param text the text content for the bullet item
     * @param level the nesting level (0-3)
     * @throws IllegalArgumentException if text is null
     */
    public void addBulletListItem(String text, int level) {
        if (text == null) {
            throw new IllegalArgumentException("Bullet item text cannot be null");
        }
        if (level < 0 || level > MAX_NESTING_LEVEL) {
            throw new IllegalArgumentException("Level must be between 0 and " + MAX_NESTING_LEVEL);
        }

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setNumID(BigInteger.valueOf(NESTED_BULLET_NUM_ID));

        // Ensure PPr exists and set ilvl
        CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr == null) {
            pPr = paragraph.getCTP().addNewPPr();
        }
        CTNumPr numPr = pPr.getNumPr();
        if (numPr == null) {
            numPr = pPr.addNewNumPr();
        }
        CTDecimalNumber ilvl = numPr.getIlvl();
        if (ilvl == null) {
            ilvl = numPr.addNewIlvl();
        }
        ilvl.setVal(BigInteger.valueOf(level));

        paragraph.createRun().setText(text);
    }

    /**
     * Converts a Flexmark OrderedList AST node to Word nested numbered list paragraphs.
     * Handles nested ordered lists by recursively processing child OrderedList nodes
     * within OrderedListItem nodes with proper indentation levels.
     *
     * @param list the Flexmark OrderedList AST node to convert
     * @throws IllegalArgumentException if list is null
     */
    public void convertOrderedList(OrderedList list) {
        if (list == null) {
            throw new IllegalArgumentException("OrderedList cannot be null");
        }
        convertOrderedList(list, 0);
    }

    /**
     * Converts a Flexmark OrderedList AST node at the specified nesting level.
     *
     * @param list the Flexmark OrderedList AST node to convert
     * @param level the nesting level (0-3)
     */
    private void convertOrderedList(OrderedList list, int level) {
        Node child = list.getFirstChild();
        while (child != null) {
            if (child instanceof OrderedListItem listItem) {
                convertOrderedListItem(listItem, level);
            }
            child = child.getNext();
        }
    }

    /**
     * Converts a Flexmark OrderedListItem to a Word paragraph with decimal numbering
     * at the specified nesting level. Recursively processes any nested OrderedList nodes.
     *
     * @param listItem the Flexmark OrderedListItem to convert
     * @param level the nesting level (0-3)
     */
    private void convertOrderedListItem(OrderedListItem listItem, int level) {
        String text = getTextFromNode(listItem);
        addOrderedListItem(text, level);

        // Check for nested ordered lists within this list item
        Node child = listItem.getFirstChild();
        while (child != null) {
            if (child instanceof OrderedList nestedList && level < MAX_NESTING_LEVEL) {
                convertOrderedList(nestedList, level + 1);
            }
            child = child.getNext();
        }
    }

    /**
     * Adds an ordered list item with the specified text to the document at the given level.
     * The item will be formatted with decimal numbering appropriate for the level.
     *
     * @param text the text content for the numbered item
     * @param level the nesting level (0-3)
     * @throws IllegalArgumentException if text is null
     */
    public void addOrderedListItem(String text, int level) {
        if (text == null) {
            throw new IllegalArgumentException("Ordered list item text cannot be null");
        }
        if (level < 0 || level > MAX_NESTING_LEVEL) {
            throw new IllegalArgumentException("Level must be between 0 and " + MAX_NESTING_LEVEL);
        }

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setNumID(BigInteger.valueOf(NESTED_ORDERED_NUM_ID));

        // Ensure PPr exists and set ilvl
        CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr == null) {
            pPr = paragraph.getCTP().addNewPPr();
        }
        CTNumPr numPr = pPr.getNumPr();
        if (numPr == null) {
            numPr = pPr.addNewNumPr();
        }
        CTDecimalNumber ilvl = numPr.getIlvl();
        if (ilvl == null) {
            ilvl = numPr.addNewIlvl();
        }
        ilvl.setVal(BigInteger.valueOf(level));

        paragraph.createRun().setText(text);
    }

    /**
     * Extracts the text content from a Flexmark AST node, excluding nested lists.
     * This method recursively visits child nodes to collect all text content,
     * but stops when encountering a BulletList or OrderedList (nested list content).
     *
     * @param node the AST node to extract text from
     * @return the concatenated text content of the node and its non-list children
     */
    private String getTextFromNode(Node node) {
        StringBuilder textBuilder = new StringBuilder();
        appendTextFromNode(node, textBuilder);
        return textBuilder.toString();
    }

    /**
     * Recursively appends text from a node and its children to a StringBuilder.
     * Stops processing when encountering a BulletList or OrderedList (nested lists).
     *
     * @param node the node to extract text from
     * @param builder the StringBuilder to append text to
     */
    private void appendTextFromNode(Node node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        // Stop at nested BulletList or OrderedList - these are processed separately
        if (node instanceof BulletList || node instanceof OrderedList) {
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
     * Ensures that the document has a numbering definition for nested bullet lists.
     * Creates a multi-level bullet numbering definition if one doesn't exist.
     * This method creates an abstract numbering definition with different bullet styles
     * for each level: bullet, circle, square, and dash.
     */
    private void ensureBulletNumberingDefinition() {
        XWPFNumbering numbering = document.getNumbering();
        if (numbering == null) {
            numbering = document.createNumbering();
        }

        // Check if the bullet numbering definition already exists
        if (numbering.getNum(BigInteger.valueOf(NESTED_BULLET_NUM_ID)) != null) {
            return;
        }

        // Create an abstract numbering definition with multi-level bullet formatting
        CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();

        // Define 4 levels of bullet nesting (0-3)
        String[] bulletChars = {"•", "o", "-", "•"};
        for (int level = 0; level <= MAX_NESTING_LEVEL; level++) {
            CTLvl lvl = abstractNum.addNewLvl();
            // The ilvl value is determined by the array index, not set explicitly

            CTDecimalNumber start = lvl.addNewStart();
            start.setVal(BigInteger.ONE);

            CTNumFmt numFmt = lvl.addNewNumFmt();
            numFmt.setVal(STNumberFormat.BULLET);

            CTLevelText lvlText = lvl.addNewLvlText();
            lvlText.setVal(bulletChars[level]);
        }

        // Wrap the CTAbstractNum in XWPFAbstractNum
        XWPFAbstractNum xwpfAbstractNum = new XWPFAbstractNum(abstractNum);
        xwpfAbstractNum.setNumbering(numbering);

        // Add the abstract numbering definition and get its ID
        BigInteger abstractNumId = numbering.addAbstractNum(xwpfAbstractNum);

        // Link the abstract numbering to a concrete numbering instance with ID 3
        numbering.addNum(abstractNumId);
    }

    /**
     * Ensures that the document has a numbering definition for nested ordered lists.
     * Creates a multi-level decimal numbering definition if one doesn't exist.
     * This method creates an abstract numbering definition with multi-level decimal
     * numbering: 1., 1.1, 1.1.1, 1.1.1.1.
     */
    private void ensureOrderedNumberingDefinition() {
        XWPFNumbering numbering = document.getNumbering();
        if (numbering == null) {
            numbering = document.createNumbering();
        }

        // Check if the ordered numbering definition already exists
        if (numbering.getNum(BigInteger.valueOf(NESTED_ORDERED_NUM_ID)) != null) {
            return;
        }

        // Create an abstract numbering definition with multi-level decimal formatting
        CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();

        // Define 4 levels of ordered nesting (0-3)
        String[] levelFormats = {"%1.", "%1.%2", "%1.%2.%3", "%1.%2.%3.%4"};
        for (int level = 0; level <= MAX_NESTING_LEVEL; level++) {
            CTLvl lvl = abstractNum.addNewLvl();
            // The ilvl value is determined by the array index, not set explicitly

            CTDecimalNumber start = lvl.addNewStart();
            start.setVal(BigInteger.ONE);

            CTNumFmt numFmt = lvl.addNewNumFmt();
            numFmt.setVal(STNumberFormat.DECIMAL);

            CTLevelText lvlText = lvl.addNewLvlText();
            lvlText.setVal(levelFormats[level]);
        }

        // Wrap the CTAbstractNum in XWPFAbstractNum
        XWPFAbstractNum xwpfAbstractNum = new XWPFAbstractNum(abstractNum);
        xwpfAbstractNum.setNumbering(numbering);

        // Add the abstract numbering definition and get its ID
        BigInteger abstractNumId = numbering.addAbstractNum(xwpfAbstractNum);

        // Link the abstract numbering to a concrete numbering instance with ID 4
        numbering.addNum(abstractNumId);
    }
}
