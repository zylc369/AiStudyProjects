package com.markdowntoword.converter;

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
}
