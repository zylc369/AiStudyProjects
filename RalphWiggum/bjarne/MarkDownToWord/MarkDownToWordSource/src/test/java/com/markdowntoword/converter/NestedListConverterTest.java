package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlCursor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NestedListConverter.
 */
class NestedListConverterTest {

    @TempDir
    Path tempDir;

    private XWPFDocument document;

    @AfterEach
    void cleanup() throws IOException {
        if (document != null) {
            document.close();
        }
    }

    @Test
    void constructor_initializesDocument() throws IOException {
        XWPFDocument doc = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new NestedListConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addBulletListItem_withLevel0_createsParagraphWithIlvl0() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addBulletListItem("Level 0 item", 0);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 0 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(3), numId, "Numbering ID should be 3 for nested bullets");
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)), "Level should be 0");
    }

    @Test
    void addBulletListItem_withLevel1_createsParagraphWithIlvl1() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addBulletListItem("Level 1 item", 1);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 1 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(3), numId, "Numbering ID should be 3");
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(0)), "Level should be 1");
    }

    @Test
    void addBulletListItem_withLevel2_createsParagraphWithIlvl2() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addBulletListItem("Level 2 item", 2);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 2 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(3), numId, "Numbering ID should be 3");
        assertEquals(BigInteger.valueOf(2), getIlvlValue(paragraphs.get(0)), "Level should be 2");
    }

    @Test
    void addBulletListItem_withLevel3_createsParagraphWithIlvl3() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addBulletListItem("Level 3 item", 3);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 3 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(3), numId, "Numbering ID should be 3");
        assertEquals(BigInteger.valueOf(3), getIlvlValue(paragraphs.get(0)), "Level should be 3");
    }

    @Test
    void addBulletListItem_withNegativeLevel_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addBulletListItem("test", -1)
        );

        assertEquals("Level must be between 0 and 3", exception.getMessage());
    }

    @Test
    void addBulletListItem_withLevelTooHigh_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addBulletListItem("test", 4)
        );

        assertEquals("Level must be between 0 and 3", exception.getMessage());
    }

    @Test
    void addBulletListItem_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addBulletListItem(null, 0)
        );

        assertEquals("Bullet item text cannot be null", exception.getMessage());
    }

    @Test
    void addOrderedListItem_withLevel0_createsParagraphWithIlvl0() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addOrderedListItem("Level 0 item", 0);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 0 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(4), numId, "Numbering ID should be 4 for nested ordered");
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)), "Level should be 0");
    }

    @Test
    void addOrderedListItem_withLevel1_createsParagraphWithIlvl1() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addOrderedListItem("Level 1 item", 1);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 1 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(4), numId, "Numbering ID should be 4");
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(0)), "Level should be 1");
    }

    @Test
    void addOrderedListItem_withLevel2_createsParagraphWithIlvl2() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addOrderedListItem("Level 2 item", 2);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 2 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(4), numId, "Numbering ID should be 4");
        assertEquals(BigInteger.valueOf(2), getIlvlValue(paragraphs.get(0)), "Level should be 2");
    }

    @Test
    void addOrderedListItem_withLevel3_createsParagraphWithIlvl3() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addOrderedListItem("Level 3 item", 3);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Level 3 item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID");
        assertEquals(BigInteger.valueOf(4), numId, "Numbering ID should be 4");
        assertEquals(BigInteger.valueOf(3), getIlvlValue(paragraphs.get(0)), "Level should be 3");
    }

    @Test
    void addOrderedListItem_withNegativeLevel_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addOrderedListItem("test", -1)
        );

        assertEquals("Level must be between 0 and 3", exception.getMessage());
    }

    @Test
    void addOrderedListItem_withLevelTooHigh_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addOrderedListItem("test", 4)
        );

        assertEquals("Level must be between 0 and 3", exception.getMessage());
    }

    @Test
    void addOrderedListItem_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addOrderedListItem(null, 0)
        );

        assertEquals("Ordered list item text cannot be null", exception.getMessage());
    }

    @Test
    void convertBulletList_with2LevelNestedList_createsCorrectParagraphs() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "- Level 0 item 1\n" +
            "  - Level 1 item 1\n" +
            "  - Level 1 item 2\n" +
            "- Level 0 item 2"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(4, paragraphs.size());

        assertEquals("Level 0 item 1", paragraphs.get(0).getText());
        assertEquals("Level 1 item 1", paragraphs.get(1).getText());
        assertEquals("Level 1 item 2", paragraphs.get(2).getText());
        assertEquals("Level 0 item 2", paragraphs.get(3).getText());

        assertEquals(BigInteger.valueOf(3), paragraphs.get(0).getNumID());
        assertEquals(BigInteger.valueOf(3), paragraphs.get(1).getNumID());
        assertEquals(BigInteger.valueOf(3), paragraphs.get(2).getNumID());
        assertEquals(BigInteger.valueOf(3), paragraphs.get(3).getNumID());

        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(1)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(2)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(3)));
    }

    @Test
    void convertBulletList_with3LevelNestedList_createsCorrectParagraphs() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "- Level 0 item 1\n" +
            "  - Level 1 item 1\n" +
            "    - Level 2 item 1\n" +
            "    - Level 2 item 2\n" +
            "  - Level 1 item 2\n" +
            "- Level 0 item 2"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(6, paragraphs.size());

        assertEquals("Level 0 item 1", paragraphs.get(0).getText());
        assertEquals("Level 1 item 1", paragraphs.get(1).getText());
        assertEquals("Level 2 item 1", paragraphs.get(2).getText());
        assertEquals("Level 2 item 2", paragraphs.get(3).getText());
        assertEquals("Level 1 item 2", paragraphs.get(4).getText());
        assertEquals("Level 0 item 2", paragraphs.get(5).getText());

        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(1)));
        assertEquals(BigInteger.valueOf(2), getIlvlValue(paragraphs.get(2)));
        assertEquals(BigInteger.valueOf(2), getIlvlValue(paragraphs.get(3)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(4)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(5)));
    }

    @Test
    void convertBulletList_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertBulletList(null)
        );

        assertEquals("BulletList cannot be null", exception.getMessage());
    }

    @Test
    void convertOrderedList_with2LevelNestedList_createsCorrectParagraphs() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "1. Level 0 item 1\n" +
            "   1. Level 1 item 1\n" +
            "   2. Level 1 item 2\n" +
            "2. Level 0 item 2"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(4, paragraphs.size());

        assertEquals("Level 0 item 1", paragraphs.get(0).getText());
        assertEquals("Level 1 item 1", paragraphs.get(1).getText());
        assertEquals("Level 1 item 2", paragraphs.get(2).getText());
        assertEquals("Level 0 item 2", paragraphs.get(3).getText());

        assertEquals(BigInteger.valueOf(4), paragraphs.get(0).getNumID());
        assertEquals(BigInteger.valueOf(4), paragraphs.get(1).getNumID());
        assertEquals(BigInteger.valueOf(4), paragraphs.get(2).getNumID());
        assertEquals(BigInteger.valueOf(4), paragraphs.get(3).getNumID());

        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(1)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(2)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(3)));
    }

    @Test
    void convertOrderedList_with3LevelNestedList_createsCorrectParagraphs() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "1. Level 0 item 1\n" +
            "   1. Level 1 item 1\n" +
            "      1. Level 2 item 1\n" +
            "      2. Level 2 item 2\n" +
            "   2. Level 1 item 2\n" +
            "2. Level 0 item 2"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(6, paragraphs.size());

        assertEquals("Level 0 item 1", paragraphs.get(0).getText());
        assertEquals("Level 1 item 1", paragraphs.get(1).getText());
        assertEquals("Level 2 item 1", paragraphs.get(2).getText());
        assertEquals("Level 2 item 2", paragraphs.get(3).getText());
        assertEquals("Level 1 item 2", paragraphs.get(4).getText());
        assertEquals("Level 0 item 2", paragraphs.get(5).getText());

        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(1)));
        assertEquals(BigInteger.valueOf(2), getIlvlValue(paragraphs.get(2)));
        assertEquals(BigInteger.valueOf(2), getIlvlValue(paragraphs.get(3)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(4)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(5)));
    }

    @Test
    void convertOrderedList_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertOrderedList(null)
        );

        assertEquals("OrderedList cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedNestedLists_createsCorrectParagraphs() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "- Bullet item 1\n" +
            "  - Nested bullet item 1\n" +
            "  - Nested bullet item 2\n" +
            "- Bullet item 2\n\n" +
            "1. Ordered item 1\n" +
            "   1. Nested ordered item 1\n" +
            "   2. Nested ordered item 2\n" +
            "2. Ordered item 2"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(8, paragraphs.size());

        assertEquals("Bullet item 1", paragraphs.get(0).getText());
        assertEquals("Nested bullet item 1", paragraphs.get(1).getText());
        assertEquals("Nested bullet item 2", paragraphs.get(2).getText());
        assertEquals("Bullet item 2", paragraphs.get(3).getText());
        assertEquals("Ordered item 1", paragraphs.get(4).getText());
        assertEquals("Nested ordered item 1", paragraphs.get(5).getText());
        assertEquals("Nested ordered item 2", paragraphs.get(6).getText());
        assertEquals("Ordered item 2", paragraphs.get(7).getText());

        // First 4 should have bullet numbering (numID 3)
        assertEquals(BigInteger.valueOf(3), paragraphs.get(0).getNumID());
        assertEquals(BigInteger.valueOf(3), paragraphs.get(1).getNumID());
        assertEquals(BigInteger.valueOf(3), paragraphs.get(2).getNumID());
        assertEquals(BigInteger.valueOf(3), paragraphs.get(3).getNumID());

        // Last 4 should have ordered numbering (numID 4)
        assertEquals(BigInteger.valueOf(4), paragraphs.get(4).getNumID());
        assertEquals(BigInteger.valueOf(4), paragraphs.get(5).getNumID());
        assertEquals(BigInteger.valueOf(4), paragraphs.get(6).getNumID());
        assertEquals(BigInteger.valueOf(4), paragraphs.get(7).getNumID());

        // Check levels
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(1)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(2)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(3)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(4)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(5)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(6)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(7)));
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void savedDocument_reopened_nestedListFormattingPersists() throws IOException {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        converter.addBulletListItem("Level 0 item", 0);
        converter.addBulletListItem("Level 1 item", 1);
        converter.addBulletListItem("Level 2 item", 2);

        Path testFile = tempDir.resolve("test-nested.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(3, paragraphs.size());

            assertEquals("Level 0 item", paragraphs.get(0).getText());
            assertEquals("Level 1 item", paragraphs.get(1).getText());
            assertEquals("Level 2 item", paragraphs.get(2).getText());

            assertEquals(BigInteger.valueOf(3), paragraphs.get(0).getNumID());
            assertEquals(BigInteger.valueOf(3), paragraphs.get(1).getNumID());
            assertEquals(BigInteger.valueOf(3), paragraphs.get(2).getNumID());

            assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)));
            assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(1)));
            assertEquals(BigInteger.valueOf(2), getIlvlValue(paragraphs.get(2)));
        }
    }

    @Test
    void convertDocument_withSingleLevelList_createsCorrectParagraphs() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "- Item 1\n" +
            "- Item 2\n" +
            "- Item 3"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("Item 1", paragraphs.get(0).getText());
        assertEquals("Item 2", paragraphs.get(1).getText());
        assertEquals("Item 3", paragraphs.get(2).getText());

        for (XWPFParagraph paragraph : paragraphs) {
            assertEquals(BigInteger.valueOf(3), paragraph.getNumID());
            assertEquals(BigInteger.ZERO, getIlvlValue(paragraph));
        }
    }

    @Test
    void convertDocument_withNoLists_createsNoParagraphs() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No lists here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyLists() {
        document = new XWPFDocument();
        NestedListConverter converter = new NestedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n\n" +
            "- First item\n" +
            "  - Nested item\n" +
            "- Second item\n\n" +
            "More text here.\n\n" +
            "1. Third item\n" +
            "2. Fourth item\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(5, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Nested item", paragraphs.get(1).getText());
        assertEquals("Second item", paragraphs.get(2).getText());
        assertEquals("Third item", paragraphs.get(3).getText());
        assertEquals("Fourth item", paragraphs.get(4).getText());

        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(0)));
        assertEquals(BigInteger.ONE, getIlvlValue(paragraphs.get(1)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(2)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(3)));
        assertEquals(BigInteger.ZERO, getIlvlValue(paragraphs.get(4)));
    }

    /**
     * Helper method to get the ilvl (indentation level) value from a paragraph.
     *
     * @param paragraph the XWPFParagraph to get the ilvl from
     * @return the ilvl value, or null if not set
     */
    private BigInteger getIlvlValue(XWPFParagraph paragraph) {
        CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr != null && pPr.isSetNumPr()) {
            CTNumPr numPr = pPr.getNumPr();
            if (numPr.isSetIlvl()) {
                return numPr.getIlvl().getVal();
            }
        }
        return null;
    }
}
