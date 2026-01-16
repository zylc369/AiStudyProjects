package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.OrderedList;
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
 * Unit tests for OrderedListConverter.
 */
class OrderedListConverterTest {

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
        OrderedListConverter converter = new OrderedListConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderedListConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addOrderedListItem_withValidText_createsParagraphWithDecimalNumbering() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        converter.addOrderedListItem("First item");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("First item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for decimal formatting");
        assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
    }

    @Test
    void addOrderedListItem_withEmptyText_createsParagraphWithDecimalNumbering() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        converter.addOrderedListItem("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for decimal formatting");
    }

    @Test
    void addOrderedListItem_withSpecialCharacters_createsParagraphWithDecimalNumbering() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        String specialText = "Item with <special> & \"characters\"";
        converter.addOrderedListItem(specialText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(specialText, paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for decimal formatting");
    }

    @Test
    void addOrderedListItem_withUnicodeCharacters_createsParagraphWithDecimalNumbering() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        String unicodeText = "Item with emoji:  and symbols: © 2024";
        converter.addOrderedListItem(unicodeText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(unicodeText, paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for decimal formatting");
    }

    @Test
    void addOrderedListItem_withMultipleItems_sequentially() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        converter.addOrderedListItem("First item");
        converter.addOrderedListItem("Second item");
        converter.addOrderedListItem("Third item");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());

        // Verify all have decimal numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for decimal formatting");
            assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
        }
    }

    @Test
    void addOrderedListItem_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addOrderedListItem(null)
        );

        assertEquals("Ordered list item text cannot be null", exception.getMessage());
    }

    @Test
    void addOrderedListItem_withLongText_createsParagraphWithDecimalNumbering() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        String longText = "This is a very long numbered item that contains multiple sentences " +
                          "and continues for quite some time to test that the converter " +
                          "can handle longer text content without any issues.";

        converter.addOrderedListItem(longText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(longText, paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for decimal formatting");
    }

    @Test
    void convertOrderedList_withSimpleList_createsNumberedParagraphs() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        OrderedList list = (OrderedList) parser.parse("1. First item\n2. Second item\n3. Third item").getFirstChild();

        converter.convertOrderedList(list);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());

        // Verify all have decimal numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for decimal formatting");
            assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
        }
    }

    @Test
    void convertOrderedList_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertOrderedList(null)
        );

        assertEquals("OrderedList cannot be null", exception.getMessage());
    }

    @Test
    void convertOrderedList_withSingleItem_createsSingleNumberedParagraph() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        OrderedList list = (OrderedList) parser.parse("1. Single item").getFirstChild();

        converter.convertOrderedList(list);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Single item", paragraphs.get(0).getText());

        BigInteger numId = paragraphs.get(0).getNumID();
        assertNotNull(numId, "Paragraph should have numbering ID for decimal formatting");
        assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
    }

    @Test
    void convertOrderedList_withEmptyList_noParagraphsCreated() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("");

        // Empty markdown produces no OrderedList nodes
        Node firstChild = doc.getFirstChild();
        if (firstChild instanceof OrderedList list) {
            converter.convertOrderedList(list);
        }

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertOrderedList_withParenthesesFormat_createsNumberedParagraphs() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        OrderedList list = (OrderedList) parser.parse("1) First item\n2) Second item").getFirstChild();

        converter.convertOrderedList(list);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(2, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());

        // Verify all have decimal numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for decimal formatting");
            assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
        }
    }

    @Test
    void convertDocument_withMultipleLists_convertsAllLists() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "1. First list item 1\n" +
            "2. First list item 2\n\n" +
            "1. Second list item 1\n" +
            "2. Second list item 2\n\n" +
            "1. Third list item 1\n" +
            "2. Third list item 2"
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

        // Verify all have decimal numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for decimal formatting");
            assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
        }
    }

    @Test
    void convertDocument_withNoLists_createsNoParagraphs() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No numbered lists here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyOrderedLists() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n\n" +
            "1. First item\n" +
            "2. Second item\n\n" +
            "More text here.\n\n" +
            "1. Third item\n" +
            "2. Fourth item\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(4, paragraphs.size());

        assertEquals("First item", paragraphs.get(0).getText());
        assertEquals("Second item", paragraphs.get(1).getText());
        assertEquals("Third item", paragraphs.get(2).getText());
        assertEquals("Fourth item", paragraphs.get(3).getText());

        // Verify all have decimal numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for decimal formatting");
            assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
        }
    }

    @Test
    void savedDocument_reopened_decimalNumberingPersists() throws IOException {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        converter.addOrderedListItem("First item");
        converter.addOrderedListItem("Second item");
        converter.addOrderedListItem("Third item");

        Path testFile = tempDir.resolve("test-numbered.docx");
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

            // Verify all have decimal numbering
            for (XWPFParagraph paragraph : paragraphs) {
                BigInteger numId = paragraph.getNumID();
                assertNotNull(numId, "Numbering ID should persist after save");
                assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
            }
        }
    }

    @Test
    void decimalNumberingVerification_checksNumIdIsSet() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        converter.addOrderedListItem("test item");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        XWPFParagraph paragraph = paragraphs.get(0);

        BigInteger numId = paragraph.getNumID();
        assertNotNull(numId, "Numbered item should have numbering ID set");
        assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
    }

    @Test
    void convertDocument_withFullMarkdownDocument_createsCorrectWordNumberedLists() {
        document = new XWPFDocument();
        OrderedListConverter converter = new OrderedListConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "# Heading One\n\n" +
            "1. First numbered\n" +
            "2. Second numbered\n\n" +
            "Some text between lists.\n\n" +
            "1. Third numbered\n" +
            "2. Fourth numbered\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(4, paragraphs.size());

        assertEquals("First numbered", paragraphs.get(0).getText());
        assertEquals("Second numbered", paragraphs.get(1).getText());
        assertEquals("Third numbered", paragraphs.get(2).getText());
        assertEquals("Fourth numbered", paragraphs.get(3).getText());

        // Verify all have decimal numbering
        for (XWPFParagraph paragraph : paragraphs) {
            BigInteger numId = paragraph.getNumID();
            assertNotNull(numId, "All paragraphs should have numbering ID for decimal formatting");
            assertEquals(BigInteger.valueOf(2), numId, "Numbering ID should be 2");
        }
    }
}
