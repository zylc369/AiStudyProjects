package com.markdowntoword.util;

import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility class for verifying formatting in converted Word documents.
 * This class provides methods to inspect and validate that Word documents
 * have equivalent formatting to the original Markdown content.
 */
public class FormatVerifier {

    private static final String HEADING_STYLE_PREFIX = "Heading";
    private static final String MONOSPACE_FONT_FAMILY = "Courier New";
    private static final int BULLET_NUM_ID = 1;
    private static final int ORDERED_NUM_ID = 2;

    /**
     * Opens a Word document for verification.
     *
     * @param wordPath the path to the Word document
     * @return the opened XWPFDocument
     * @throws IOException if the file cannot be read
     */
    public static XWPFDocument openDocument(Path wordPath) throws IOException {
        FileInputStream inputStream = new FileInputStream(wordPath.toFile());
        return new XWPFDocument(inputStream);
    }

    /**
     * Verifies that a paragraph has a heading style.
     *
     * @param paragraph the paragraph to verify
     * @param level     the expected heading level (1-6)
     * @return true if the paragraph has the expected heading style, false otherwise
     */
    public static boolean hasHeadingStyle(XWPFParagraph paragraph, int level) {
        String style = paragraph.getStyle();
        return style != null && style.equals(HEADING_STYLE_PREFIX + level);
    }

    /**
     * Verifies that a run has bold formatting.
     *
     * @param run the run to verify
     * @return true if the run is bold, false otherwise
     */
    public static boolean isBold(XWPFRun run) {
        Boolean bold = run.isBold();
        return bold != null && bold;
    }

    /**
     * Verifies that a run has italic formatting.
     *
     * @param run the run to verify
     * @return true if the run is italic, false otherwise
     */
    public static boolean isItalic(XWPFRun run) {
        Boolean italic = run.isItalic();
        return italic != null && italic;
    }

    /**
     * Verifies that a run uses monospace font.
     *
     * @param run the run to verify
     * @return true if the run uses monospace font, false otherwise
     */
    public static boolean isMonospace(XWPFRun run) {
        String fontFamily = run.getFontFamily();
        return fontFamily != null && fontFamily.equalsIgnoreCase(MONOSPACE_FONT_FAMILY);
    }

    /**
     * Counts the number of paragraphs with a specific heading style in a document.
     *
     * @param document the document to inspect
     * @param level    the heading level (1-6)
     * @return the number of paragraphs with the specified heading style
     */
    public static int countHeadingStyle(XWPFDocument document, int level) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (hasHeadingStyle(paragraph, level)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of runs with bold formatting in a document.
     *
     * @param document the document to inspect
     * @return the number of runs with bold formatting
     */
    public static int countBoldRuns(XWPFDocument document) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                if (isBold(run)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts the number of runs with italic formatting in a document.
     *
     * @param document the document to inspect
     * @return the number of runs with italic formatting
     */
    public static int countItalicRuns(XWPFDocument document) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                if (isItalic(run)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts the number of runs with monospace font in a document.
     *
     * @param document the document to inspect
     * @return the number of runs with monospace font
     */
    public static int countMonospaceRuns(XWPFDocument document) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                if (isMonospace(run)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts the number of hyperlinks in a document.
     *
     * @param document the document to inspect
     * @return the number of hyperlinks
     */
    public static int countHyperlinks(XWPFDocument document) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink[] hyperlinks =
                    paragraph.getCTP().getHyperlinkArray();
            count += hyperlinks.length;
        }
        return count;
    }

    /**
     * Verifies that a hyperlink exists with the specified URL in a document.
     *
     * @param document the document to inspect
     * @param url      the URL to search for
     * @return true if a hyperlink with the specified URL exists, false otherwise
     */
    public static boolean hasHyperlinkWithUrl(XWPFDocument document, String url) {
        try {
            PackageRelationshipCollection relationships = document.getPackagePart().getRelationships();
            for (PackageRelationship relationship : relationships) {
                try {
                    String targetUri = relationship.getTargetURI().toString();
                    if (url.equals(targetUri)) {
                        return true;
                    }
                } catch (Exception e) {
                    // Ignore URI parsing errors, continue checking other relationships
                }
            }
        } catch (Exception e) {
            // Ignore relationship access errors
        }
        return false;
    }

    /**
     * Counts the number of code blocks in a document.
     * Code blocks are identified by paragraphs with monospace font
     * and background shading.
     *
     * @param document the document to inspect
     * @return the number of code blocks
     */
    public static int countCodeBlocks(XWPFDocument document) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (isCodeBlock(paragraph)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Verifies that a paragraph is a code block.
     * Code blocks have monospace font and background shading.
     *
     * @param paragraph the paragraph to verify
     * @return true if the paragraph is a code block, false otherwise
     */
    public static boolean isCodeBlock(XWPFParagraph paragraph) {
        boolean hasMonospace = false;
        for (XWPFRun run : paragraph.getRuns()) {
            if (isMonospace(run)) {
                hasMonospace = true;
                break;
            }
        }

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr == null) {
            return false;
        }

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd shd = pPr.getShd();
        Object fillValue = shd != null ? shd.getFill() : null;
        boolean hasShading = shd != null && fillValue != null && !fillValue.toString().isEmpty();

        return hasMonospace && hasShading;
    }

    /**
     * Counts the number of tables in a document.
     *
     * @param document the document to inspect
     * @return the number of tables
     */
    public static int countTables(XWPFDocument document) {
        return document.getTables().size();
    }

    /**
     * Gets the first table from a document.
     *
     * @param document the document to inspect
     * @return the first table, or null if no tables exist
     */
    public static XWPFTable getFirstTable(XWPFDocument document) {
        List<XWPFTable> tables = document.getTables();
        if (tables.isEmpty()) {
            return null;
        }
        return tables.get(0);
    }

    /**
     * Verifies that a table has at least the specified number of rows.
     *
     * @param table      the table to inspect
     * @param minRows    the minimum expected number of rows
     * @return true if the table has at least minRows rows, false otherwise
     */
    public static boolean hasMinimumRows(XWPFTable table, int minRows) {
        return table.getNumberOfRows() >= minRows;
    }

    /**
     * Verifies that a table has at least the specified number of columns.
     *
     * @param table       the table to inspect
     * @param minColumns  the minimum expected number of columns
     * @return true if the table has at least minColumns columns, false otherwise
     */
    public static boolean hasMinimumColumns(XWPFTable table, int minColumns) {
        if (table.getNumberOfRows() == 0) {
            return false;
        }
        XWPFTableRow firstRow = table.getRow(0);
        return firstRow.getTableCells().size() >= minColumns;
    }

    /**
     * Counts the number of paragraphs with bullet list numbering in a document.
     *
     * @param document the document to inspect
     * @return the number of bullet list paragraphs
     */
    public static int countBulletListParagraphs(XWPFDocument document) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (isBulletList(paragraph)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Verifies that a paragraph has bullet list formatting.
     * Checks the CTP (paragraph properties) for the numID attribute.
     *
     * @param paragraph the paragraph to verify
     * @return true if the paragraph has bullet list formatting, false otherwise
     */
    public static boolean isBulletList(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        if (ctp == null || ctp.getPPr() == null) {
            return false;
        }
        if (ctp.getPPr().getNumPr() == null) {
            return false;
        }
        if (ctp.getPPr().getNumPr().getNumId() == null) {
            return false;
        }
        String numId = ctp.getPPr().getNumPr().getNumId().toString();
        return numId != null && numId.equals(String.valueOf(BULLET_NUM_ID));
    }

    /**
     * Counts the number of paragraphs with ordered list numbering in a document.
     *
     * @param document the document to inspect
     * @return the number of ordered list paragraphs
     */
    public static int countOrderedListParagraphs(XWPFDocument document) {
        int count = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (isOrderedList(paragraph)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Verifies that a paragraph has ordered list formatting.
     * Checks the CTP (paragraph properties) for the numID attribute.
     *
     * @param paragraph the paragraph to verify
     * @return true if the paragraph has ordered list formatting, false otherwise
     */
    public static boolean isOrderedList(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        if (ctp == null || ctp.getPPr() == null) {
            return false;
        }
        if (ctp.getPPr().getNumPr() == null) {
            return false;
        }
        if (ctp.getPPr().getNumPr().getNumId() == null) {
            return false;
        }
        String numId = ctp.getPPr().getNumPr().getNumId().toString();
        return numId != null && numId.equals(String.valueOf(ORDERED_NUM_ID));
    }

    /**
     * Finds the first paragraph containing the specified text.
     *
     * @param document the document to search
     * @param text     the text to search for
     * @return the first paragraph containing the text, or null if not found
     */
    public static XWPFParagraph findParagraphWithText(XWPFDocument document, String text) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (paragraph.getText().contains(text)) {
                return paragraph;
            }
        }
        return null;
    }
}
