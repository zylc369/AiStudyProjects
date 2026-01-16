package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
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
 * Unit tests for HeadingConverter.
 */
class HeadingConverterTest {

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
        HeadingConverter converter = new HeadingConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new HeadingConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addHeading_withLevel1_createsParagraphWithHeading1Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Title", 1);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading1", paragraph.getStyle());
        assertEquals("Title", paragraph.getText());
    }

    @Test
    void addHeading_withLevel2_createsParagraphWithHeading2Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Chapter", 2);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading2", paragraph.getStyle());
        assertEquals("Chapter", paragraph.getText());
    }

    @Test
    void addHeading_withLevel3_createsParagraphWithHeading3Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Section", 3);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading3", paragraph.getStyle());
        assertEquals("Section", paragraph.getText());
    }

    @Test
    void addHeading_withLevel4_createsParagraphWithHeading4Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Subsection", 4);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading4", paragraph.getStyle());
        assertEquals("Subsection", paragraph.getText());
    }

    @Test
    void addHeading_withLevel5_createsParagraphWithHeading5Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Topic", 5);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading5", paragraph.getStyle());
        assertEquals("Topic", paragraph.getText());
    }

    @Test
    void addHeading_withLevel6_createsParagraphWithHeading6Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Detail", 6);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading6", paragraph.getStyle());
        assertEquals("Detail", paragraph.getText());
    }

    @Test
    void addHeading_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHeading(null, 1)
        );

        assertEquals("Heading text cannot be null", exception.getMessage());
    }

    @Test
    void addHeading_withLevelZero_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHeading("Title", 0)
        );

        assertEquals("Heading level cannot be less than 1, was: 0", exception.getMessage());
    }

    @Test
    void addHeading_withNegativeLevel_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHeading("Title", -1)
        );

        assertEquals("Heading level cannot be less than 1, was: -1", exception.getMessage());
    }

    @Test
    void addHeading_withLevel7_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHeading("Title", 7)
        );

        assertEquals("Heading level cannot be greater than 6, was: 7", exception.getMessage());
    }

    @Test
    void addHeading_withLevel100_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHeading("Title", 100)
        );

        assertEquals("Heading level cannot be greater than 6, was: 100", exception.getMessage());
    }

    @Test
    void addHeading_multipleHeadings_sequentially() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Title", 1);
        converter.addHeading("Chapter One", 2);
        converter.addHeading("Section 1.1", 3);
        converter.addHeading("Subsection", 4);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(4, paragraphs.size());

        assertEquals("Heading1", paragraphs.get(0).getStyle());
        assertEquals("Title", paragraphs.get(0).getText());

        assertEquals("Heading2", paragraphs.get(1).getStyle());
        assertEquals("Chapter One", paragraphs.get(1).getText());

        assertEquals("Heading3", paragraphs.get(2).getStyle());
        assertEquals("Section 1.1", paragraphs.get(2).getText());

        assertEquals("Heading4", paragraphs.get(3).getStyle());
        assertEquals("Subsection", paragraphs.get(3).getText());
    }

    @Test
    void savedDocument_reopened_headingsArePreserved() throws IOException {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("Main Title", 1);
        converter.addHeading("Section Title", 2);

        Path testFile = tempDir.resolve("test-headings.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        // Reopen the saved document
        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(2, paragraphs.size());

            assertEquals("Heading1", paragraphs.get(0).getStyle());
            assertEquals("Main Title", paragraphs.get(0).getText());

            assertEquals("Heading2", paragraphs.get(1).getStyle());
            assertEquals("Section Title", paragraphs.get(1).getText());
        }
    }

    @Test
    void addHeading_withEmptyText_createsParagraphWithCorrectStyle() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        converter.addHeading("", 1);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading1", paragraph.getStyle());
        assertEquals("", paragraph.getText());
    }

    @Test
    void addHeading_withSpecialCharactersInText_createsParagraphWithCorrectContent() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        String specialText = "Chapter 1.2: <Test & Review>";
        converter.addHeading(specialText, 2);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading2", paragraph.getStyle());
        assertEquals(specialText, paragraph.getText());
    }

    @Test
    void convertHeading_withLevel1_createsParagraphWithHeading1Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Heading heading = (Heading) parser.parse("# Main Title").getFirstChild();

        converter.convertHeading(heading);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading1", paragraph.getStyle());
        assertEquals("Main Title", paragraph.getText());
    }

    @Test
    void convertHeading_withLevel2_createsParagraphWithHeading2Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Heading heading = (Heading) parser.parse("## Chapter One").getFirstChild();

        converter.convertHeading(heading);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading2", paragraph.getStyle());
        assertEquals("Chapter One", paragraph.getText());
    }

    @Test
    void convertHeading_withLevel3_createsParagraphWithHeading3Style() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Heading heading = (Heading) parser.parse("### Section 1.1").getFirstChild();

        converter.convertHeading(heading);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Heading3", paragraph.getStyle());
        assertEquals("Section 1.1", paragraph.getText());
    }

    @Test
    void convertHeading_withMultipleHeadings_sequentially() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();

        Heading heading1 = (Heading) parser.parse("# Title").getFirstChild();
        Heading heading2 = (Heading) parser.parse("## Chapter One").getFirstChild();
        Heading heading3 = (Heading) parser.parse("### Section 1.1").getFirstChild();

        converter.convertHeading(heading1);
        converter.convertHeading(heading2);
        converter.convertHeading(heading3);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("Heading1", paragraphs.get(0).getStyle());
        assertEquals("Title", paragraphs.get(0).getText());

        assertEquals("Heading2", paragraphs.get(1).getStyle());
        assertEquals("Chapter One", paragraphs.get(1).getText());

        assertEquals("Heading3", paragraphs.get(2).getStyle());
        assertEquals("Section 1.1", paragraphs.get(2).getText());
    }

    @Test
    void convertHeading_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertHeading(null)
        );

        assertEquals("Heading cannot be null", exception.getMessage());
    }

    @Test
    void convertHeadingTextExtraction_withSimpleText_correctlyExtractsText() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Heading heading = (Heading) parser.parse("# Simple Heading Text").getFirstChild();

        converter.convertHeading(heading);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Simple Heading Text", paragraphs.get(0).getText());
    }

    @Test
    void convertHeadingTextExtraction_withSpecialCharacters_correctlyExtractsText() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        Heading heading = (Heading) parser.parse("# Chapter 1.2: <Test & Review>").getFirstChild();

        converter.convertHeading(heading);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());
        assertEquals("Chapter 1.2: <Test & Review>", paragraphs.get(0).getText());
    }

    @Test
    void convertDocument_withMultipleHeadings_convertsAllHeadings() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "# Main Title\n" +
            "## Chapter One\n" +
            "### Section 1.1\n" +
            "#### Subsection\n" +
            "##### Topic\n" +
            "###### Detail"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(6, paragraphs.size());

        assertEquals("Heading1", paragraphs.get(0).getStyle());
        assertEquals("Main Title", paragraphs.get(0).getText());

        assertEquals("Heading2", paragraphs.get(1).getStyle());
        assertEquals("Chapter One", paragraphs.get(1).getText());

        assertEquals("Heading3", paragraphs.get(2).getStyle());
        assertEquals("Section 1.1", paragraphs.get(2).getText());

        assertEquals("Heading4", paragraphs.get(3).getStyle());
        assertEquals("Subsection", paragraphs.get(3).getText());

        assertEquals("Heading5", paragraphs.get(4).getStyle());
        assertEquals("Topic", paragraphs.get(4).getText());

        assertEquals("Heading6", paragraphs.get(5).getStyle());
        assertEquals("Detail", paragraphs.get(5).getText());
    }

    @Test
    void convertDocument_withNoHeadings_createsNoParagraphs() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No headings here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyHeadings() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n" +
            "# Heading One\n" +
            "More text here.\n" +
            "## Heading Two\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(2, paragraphs.size());

        assertEquals("Heading1", paragraphs.get(0).getStyle());
        assertEquals("Heading One", paragraphs.get(0).getText());

        assertEquals("Heading2", paragraphs.get(1).getStyle());
        assertEquals("Heading Two", paragraphs.get(1).getText());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void verifyHeadingConversion_withFullMarkdownDocument_createsCorrectWordParagraphs() {
        document = new XWPFDocument();
        HeadingConverter converter = new HeadingConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "# Document Title\n" +
            "Introduction paragraph.\n" +
            "## First Section\n" +
            "Section content.\n" +
            "### Subsection A\n" +
            "Subsection content.\n" +
            "### Subsection B\n" +
            "## Second Section\n" +
            "More content."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(5, paragraphs.size());

        // Verify each heading is converted with correct style and text
        assertEquals("Heading1", paragraphs.get(0).getStyle());
        assertEquals("Document Title", paragraphs.get(0).getText());

        assertEquals("Heading2", paragraphs.get(1).getStyle());
        assertEquals("First Section", paragraphs.get(1).getText());

        assertEquals("Heading3", paragraphs.get(2).getStyle());
        assertEquals("Subsection A", paragraphs.get(2).getText());

        assertEquals("Heading3", paragraphs.get(3).getStyle());
        assertEquals("Subsection B", paragraphs.get(3).getText());

        assertEquals("Heading2", paragraphs.get(4).getStyle());
        assertEquals("Second Section", paragraphs.get(4).getText());
    }
}
