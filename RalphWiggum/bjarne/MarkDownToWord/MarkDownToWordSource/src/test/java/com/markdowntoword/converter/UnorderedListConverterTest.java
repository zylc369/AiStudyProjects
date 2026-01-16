package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UnorderedListConverter.
 */
class UnorderedListConverterTest {

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
        UnorderedListConverter converter = new UnorderedListConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new UnorderedListConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addBulletListItem_withValidText_createsParagraphWithBulletNumbering() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        converter.addBulletListItem("First item");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("First item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for bullet formatting");
        assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
    }

    @Test
    void addBulletListItem_withEmptyText_createsParagraphWithBulletNumbering() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        converter.addBulletListItem("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for bullet formatting");
    }

    @Test
    void addBulletListItem_withSpecialCharacters_createsParagraphWithBulletNumbering() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        String specialText = "Item with <special> & \"characters\"";
        converter.addBulletListItem(specialText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(specialText, paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for bullet formatting");
    }

    @Test
    void addBulletListItem_withMultipleItems_sequentially() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        converter.addBulletListItem("First item");
        converter.addBulletListItem("Second item");
        converter.addBulletListItem("Third item");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());

        // Verify all have bullet numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for bullet formatting");
            assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
        }
    }

    @Test
    void addBulletListItem_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addBulletListItem(null)
        );

        assertEquals("Bullet item text cannot be null", exception.getMessage());
    }

    @Test
    void addBulletListItem_withLongText_createsParagraphWithBulletNumbering() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        String longText = "This is a very long bullet item that contains multiple sentences " +
                          "and continues for quite some time to test that the converter " +
                          "can handle longer text content without any issues.";

        converter.addBulletListItem(longText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(longText, paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for bullet formatting");
    }

    @Test
    void addBulletListItem_withUnicodeCharacters_createsParagraphWithBulletNumbering() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        String unicodeText = "Item with emoji:  and symbols: © 2024";
        converter.addBulletListItem(unicodeText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(unicodeText, paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for bullet formatting");
    }

    @Test
    void convertBulletList_withSimpleList_createsBulletParagraphs() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        BulletList list = (BulletList) parser.parse("- First item\n- Second item\n- Third item").getFirstChild();

        converter.convertBulletList(list);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());

        // Verify all have bullet numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for bullet formatting");
            assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
        }
    }

    @Test
    void convertBulletList_withAsteriskMarker_createsBulletParagraphs() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        BulletList list = (BulletList) parser.parse("* First item\n* Second item\n* Third item").getFirstChild();

        converter.convertBulletList(list);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());

        // Verify all have bullet numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for bullet formatting");
            assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
        }
    }

    @Test
    void convertBulletList_withPlusMarker_createsBulletParagraphs() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        BulletList list = (BulletList) parser.parse("+ First item\n+ Second item\n+ Third item").getFirstChild();

        converter.convertBulletList(list);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());

        // Verify all have bullet numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for bullet formatting");
            assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
        }
    }

    @Test
    void convertBulletList_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertBulletList(null)
        );

        assertEquals("BulletList cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMultipleLists_convertsAllLists() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "- First list item 1\n" +
            "- First list item 2\n\n" +
            "- Second list item 1\n" +
            "- Second list item 2\n\n" +
            "- Third list item 1\n" +
            "- Third list item 2"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(6, paragraphs.size());

        assertEquals("First list item 1", paragraphs.get(0).getText());
        assertEquals("First list item 2", paragraphs.get(1).getText());
        assertEquals("Second list item 1", paragraphs.get(2).getText());
        assertEquals("Second list item 2", paragraphs.get(3).getText());
        assertEquals("Third list item 1", paragraphs.get(4).getText());
        assertEquals("Third list item 2", paragraphs.get(5).getText());

        // Verify all have bullet numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for bullet formatting");
            assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
        }
    }

    @Test
    void convertDocument_withNoLists_createsNoParagraphs() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No bullet lists here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyBulletLists() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n\n" +
            "- First item\n" +
            "- Second item\n\n" +
            "More text here.\n\n" +
            "- Third item\n" +
            "- Fourth item\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(4, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());
        assertEquals("Fourth item", paragraphs.get(3).getText());

        // Verify all have bullet numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for bullet formatting");
            assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
        }
    }

    @Test
    void savedDocument_reopened_bulletNumberingPersists() throws IOException {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        converter.addBulletListItem("First item");
        converter.addBulletListItem("Second item");
        converter.addBulletListItem("Third item");

        Path testFile = tempDir.resolve("test-bullets.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(3, paragraphs.size());

            assertEquals("First item", paragraphs.get(0).getText());
            assertEquals("Second item", paragraphs.get(1).getText());
            assertEquals("Third item", paragraphs.get(2).getText());

            // Verify all have bullet numbering
            for (XWPFParagraph paragraph : paragraphs) {
                BigInteger numId = paragraph.getNumID();
                assertNotNull(numId, "Numbering ID should persist after save");
                assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
            }
        }
    }

    @Test
    void bulletNumberingVerification_checksNumIdIsSet() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        converter.addBulletListItem("test item");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        XWPFParagraph paragraph = paragraphs.get(0);

        BigInteger numId = paragraph.getNumID();
        assertNotNull(numId, "Bullet item should have numbering ID set");
        assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
    }

    @Test
    void convertBulletList_withSingleItem_createsSingleBulletParagraph() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        BulletList list = (BulletList) parser.parse("- Single item").getFirstChild();

        converter.convertBulletList(list);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Single item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for bullet formatting");
        assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
    }

    @Test
    void convertBulletList_withEmptyList_noParagraphsCreated() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("");

        // Empty markdown produces no BulletList nodes
        Node firstChild = doc.getFirstChild();
        if (firstChild instanceof BulletList list) {
            converter.convertBulletList(list);
        }

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withFullMarkdownDocument_createsCorrectWordBulletLists() {
        document = new XWPFDocument();
        UnorderedListConverter converter = new UnorderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "# Heading One\n\n" +
            "- First bullet\n" +
            "- Second bullet\n\n" +
            "Some text between lists.\n\n" +
            "- Third bullet\n" +
            "- Fourth bullet\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(4, paragraphs.size());

        assertEquals("First bullet", paragraphs.get(0).getText());
        assertEquals("Second bullet", paragraphs.get(1).getText());
        assertEquals("Third bullet", paragraphs.get(2).getText());
        assertEquals("Fourth bullet", paragraphs.get(3).getText());

        // Verify all have bullet numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for bullet formatting");
            assertEquals(BigInteger.ONE, numId, "Numbering ID should be 1");
        }
    }
}
