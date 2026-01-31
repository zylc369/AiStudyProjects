package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.ast.OrderedList;
import com.vladsch.flexmark.ast.OrderedListItem;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.util.ast.Node;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import java.math.BigInteger;

/**
 * Converter for list elements (bulleted and numbered lists).
 */
public class ListConverter {

    private final InlineTextConverter inlineTextConverter;

    public ListConverter() {
        this.inlineTextConverter = new InlineTextConverter();
    }

    /**
     * Converts a BulletList node to Word paragraphs with bullet formatting.
     *
     * @param bulletList The BulletList node from the AST
     * @param document The Word document to add content to
     */
    public void convertBulletList(BulletList bulletList, XWPFDocument document) {
        convertBulletList(bulletList, document, 0);
    }

    /**
     * Converts a BulletList node with specified nesting depth.
     *
     * @param bulletList The BulletList node from the AST
     * @param document The Word document to add content to
     * @param depth The nesting depth (0-2 for up to 3 levels)
     */
    public void convertBulletList(BulletList bulletList, XWPFDocument document, int depth) {
        Node child = bulletList.getFirstChild();
        while (child != null) {
            if (child instanceof BulletListItem item) {
                convertListItem(item, document, true, depth);
            }
            child = child.getNext();
        }
    }

    /**
     * Converts an OrderedList node to Word paragraphs with numbered formatting.
     *
     * @param orderedList The OrderedList node from the AST
     * @param document The Word document to add content to
     */
    public void convertOrderedList(OrderedList orderedList, XWPFDocument document) {
        convertOrderedList(orderedList, document, 0);
    }

    /**
     * Converts an OrderedList node with specified nesting depth.
     *
     * @param orderedList The OrderedList node from the AST
     * @param document The Word document to add content to
     * @param depth The nesting depth (0-2 for up to 3 levels)
     */
    public void convertOrderedList(OrderedList orderedList, XWPFDocument document, int depth) {
        Node child = orderedList.getFirstChild();
        while (child != null) {
            if (child instanceof OrderedListItem item) {
                convertListItem(item, document, false, depth);
            }
            child = child.getNext();
        }
    }

    /**
     * Converts a single ListItem to a Word paragraph with appropriate numbering.
     *
     * @param item The ListItem node
     * @param document The Word document
     * @param isBullet True for bullet list, false for numbered list
     * @param depth The nesting depth (0-2 for up to 3 levels)
     */
    private void convertListItem(ListItem item, XWPFDocument document, boolean isBullet, int depth) {
        // Limit depth to 2 (3 levels: 0, 1, 2)
        int actualDepth = Math.min(depth, 2);

        // Create a new paragraph for this list item
        XWPFParagraph paragraph = document.createParagraph();

        // Set numbering properties
        CTP ctp = paragraph.getCTP();
        CTPPr ppr = ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
        CTNumPr numPr = ppr.isSetNumPr() ? ppr.getNumPr() : ppr.addNewNumPr();

        // Set numbering level based on depth
        numPr.addNewIlvl().setVal(BigInteger.valueOf(actualDepth));

        if (isBullet) {
            // Bullet list format
            numPr.addNewNumId().setVal(BigInteger.ONE);
        } else {
            // Numbered list format
            numPr.addNewNumId().setVal(BigInteger.valueOf(2));
        }

        // Process inline content within the list item
        // List items can contain paragraphs and other inline elements
        Node itemChild = item.getFirstChild();
        while (itemChild != null) {
            if (itemChild instanceof com.vladsch.flexmark.ast.Paragraph para) {
                // Process paragraph content
                inlineTextConverter.processInlineNodes(paragraph, para.getFirstChild());
            } else if (itemChild instanceof BulletList nestedBulletList) {
                // Nested bullet list - recursively process
                convertBulletList(nestedBulletList, document, depth + 1);
            } else if (itemChild instanceof OrderedList nestedOrderedList) {
                // Nested ordered list - recursively process
                convertOrderedList(nestedOrderedList, document, depth + 1);
            } else {
                // Process other inline content
                inlineTextConverter.processInlineNodes(paragraph, itemChild);
            }
            itemChild = itemChild.getNext();
        }

        // If no inline content, add empty text to ensure bullet appears
        if (paragraph.getRuns().isEmpty()) {
            XWPFRun run = paragraph.createRun();
            run.setText("");
        }
    }
}
