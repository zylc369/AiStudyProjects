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
        Node child = bulletList.getFirstChild();
        while (child != null) {
            if (child instanceof BulletListItem item) {
                convertListItem(item, document, true);
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
        Node child = orderedList.getFirstChild();
        while (child != null) {
            if (child instanceof OrderedListItem item) {
                convertListItem(item, document, false);
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
     */
    private void convertListItem(ListItem item, XWPFDocument document, boolean isBullet) {
        // Create a new paragraph for this list item
        XWPFParagraph paragraph = document.createParagraph();

        // Set numbering properties
        CTP ctp = paragraph.getCTP();
        CTPPr ppr = ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
        CTNumPr numPr = ppr.isSetNumPr() ? ppr.getNumPr() : ppr.addNewNumPr();

        // Set numbering level (0 for top level)
        numPr.addNewIlvl().setVal(BigInteger.valueOf(0));

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
