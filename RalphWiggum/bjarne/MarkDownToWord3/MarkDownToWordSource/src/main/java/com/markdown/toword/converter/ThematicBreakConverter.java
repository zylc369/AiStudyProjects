package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.ThematicBreak;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.Borders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;

/**
 * Converter for thematic break (horizontal rule) elements.
 */
public class ThematicBreakConverter {

    /**
     * Converts a ThematicBreak node to a horizontal line in Word.
     *
     * @param thematicBreak The ThematicBreak node from the AST
     * @param document The Word document to add content to
     */
    public void convertThematicBreak(ThematicBreak thematicBreak, XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();

        // Set bottom border to create horizontal line
        paragraph.setBorderBottom(Borders.SINGLE);

        // Add spacing above and below for visual separation
        paragraph.setSpacingBefore(100);
        paragraph.setSpacingAfter(100);

        // Alternative approach using CTPBdr for more control:
        // CTP ctp = paragraph.getCTP();
        // CTPPr ppr = ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
        // CTPBdr pbdr = ppr.isSetPBdr() ? ppr.getPBdr() : ppr.addNewPBdr();
        // CTBorder bottom = pbdr.isSetBottom() ? pbdr.getBottom() : pbdr.addNewBottom();
        // bottom.setVal(STBorder.SINGLE);
        // bottom.setSz(BigInteger.valueOf(4));
    }
}
