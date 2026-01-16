package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.Paragraph;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParagraphConverter.
 */
class ParagraphConverterTest {

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
        ParagraphConverter converter = new ParagraphConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ParagraphConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addParagraph_withValidText_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        converter.addParagraph("This is a paragraph.");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("This is a paragraph.", paragraphs.get(0).getText());
    }

    @Test
    void addParagraph_withEmptyText_createsParagraphWithNoContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        converter.addParagraph("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("", paragraphs.get(0).getText());
    }

    @Test
    void addParagraph_withSpecialCharacters_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        String specialText = "Paragraph with <special> & \"characters\"";
        converter.addParagraph(specialText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(specialText, paragraphs.get(0).getText());
    }

    @Test
    void addParagraph_withMultipleParagraphs_sequentially() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        converter.addParagraph("First paragraph.");
        converter.addParagraph("Second paragraph.");
        converter.addParagraph("Third paragraph.");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First paragraph.", paragraphs.get(0).getText());
        assertEquals("Second paragraph.", paragraphs.get(1).getText());
        assertEquals("Third paragraph.", paragraphs.get(2).getText());
    }

    @Test
    void addParagraph_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addParagraph(null)
        );

        assertEquals("Paragraph text cannot be null", exception.getMessage());
    }

    @Test
    void addParagraph_withLongText_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        String longText = "This is a very long paragraph that contains multiple sentences " +
                          "and continues for quite some time to test that the converter " +
                          "can handle longer text content without any issues.";

        converter.addParagraph(longText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(longText, paragraphs.get(0).getText());
    }

    @Test
    void addParagraph_withUnicodeCharacters_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        String unicodeText = "Paragraph with emoji:  and symbols: © 2024";
        converter.addParagraph(unicodeText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(unicodeText, paragraphs.get(0).getText());
    }

    @Test
    void convertParagraph_withSimpleText_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Paragraph paragraph = (Paragraph) parser.parse("This is a paragraph.").getFirstChild();

        converter.convertParagraph(paragraph);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("This is a paragraph.", paragraphs.get(0).getText());
    }

    @Test
    void convertParagraph_withMultipleTextNodes_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Paragraph paragraph = (Paragraph) parser.parse("Paragraph with multiple words.").getFirstChild();

        converter.convertParagraph(paragraph);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Paragraph with multiple words.", paragraphs.get(0).getText());
    }

    @Test
    void convertParagraph_withSpecialCharacters_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Paragraph paragraph = (Paragraph) parser.parse("Text with & \"characters\" and symbols: ©").getFirstChild();

        converter.convertParagraph(paragraph);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Text with & \"characters\" and symbols: ©", paragraphs.get(0).getText());
    }

    @Test
    void convertParagraph_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertParagraph(null)
        );

        assertEquals("Paragraph cannot be null", exception.getMessage());
    }

    @Test
    void convertParagraph_withEmptyInput_noParagraphCreated() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("");

        // Empty markdown produces no paragraph nodes
        Node firstChild = doc.getFirstChild();
        if (firstChild instanceof Paragraph paragraph) {
            converter.convertParagraph(paragraph);
        }

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withMultipleParagraphs_convertsAllParagraphs() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "First paragraph.\n\n" +
            "Second paragraph.\n\n" +
            "Third paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First paragraph.", paragraphs.get(0).getText());
        assertEquals("Second paragraph.", paragraphs.get(1).getText());
        assertEquals("Third paragraph.", paragraphs.get(2).getText());
    }

    @Test
    void convertDocument_withNoParagraphs_createsNoParagraphs() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse("# Just headings\n## No paragraphs here");

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyParagraphs() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "# Heading One\n\n" +
            "Paragraph after heading.\n\n" +
            "## Heading Two\n\n" +
            "Another paragraph.\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("Paragraph after heading.", paragraphs.get(0).getText());
        assertEquals("Another paragraph.", paragraphs.get(1).getText());
        assertEquals("Concluding paragraph.", paragraphs.get(2).getText());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void savedDocument_reopened_paragraphsArePreserved() throws IOException {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        converter.addParagraph("First paragraph.");
        converter.addParagraph("Second paragraph.");

        Path testFile = tempDir.resolve("test-paragraphs.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        // Reopen the saved document
        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(2, paragraphs.size());

            assertEquals("First paragraph.", paragraphs.get(0).getText());
            assertEquals("Second paragraph.", paragraphs.get(1).getText());
        }
    }

    @Test
    void convertDocument_withFullMarkdownDocument_createsCorrectWordParagraphs() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is the introduction paragraph.\n\n" +
            "It has multiple sentences.\n\n" +
            "# Main Heading\n\n" +
            "Content paragraph after heading.\n\n" +
            "More content here.\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(5, paragraphs.size());

        assertEquals("This is the introduction paragraph.", paragraphs.get(0).getText());
        assertEquals("It has multiple sentences.", paragraphs.get(1).getText());
        assertEquals("Content paragraph after heading.", paragraphs.get(2).getText());
        assertEquals("More content here.", paragraphs.get(3).getText());
        assertEquals("Concluding paragraph.", paragraphs.get(4).getText());
    }

    @Test
    void convertParagraph_withLongText_extractsTextCorrectly() {
        document = new XWPFDocument();
        ParagraphConverter converter = new ParagraphConverter(document);

        String longText = "This is a very long paragraph that contains multiple sentences " +
                          "and continues for quite some time to test that the converter " +
                          "can handle longer text content without any issues.";

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Paragraph paragraph = (Paragraph) parser.parse(longText).getFirstChild();

        converter.convertParagraph(paragraph);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals(longText, paragraphs.get(0).getText());
    }
}
