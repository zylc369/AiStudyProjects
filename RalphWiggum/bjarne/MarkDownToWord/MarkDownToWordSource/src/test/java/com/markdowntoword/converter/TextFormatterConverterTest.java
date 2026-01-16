package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TextFormatterConverter.
 */
class TextFormatterConverterTest {

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
        TextFormatterConverter converter = new TextFormatterConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TextFormatterConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addBoldText_withSimpleText_createsParagraphWithBoldFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldText("bold text");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("bold text", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
    }

    @Test
    void addBoldText_withMultipleWords_createsParagraphWithBoldFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldText("this is bold text with multiple words");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("this is bold text with multiple words", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
    }

    @Test
    void addBoldText_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addBoldText(null)
        );

        assertEquals("Bold text cannot be null", exception.getMessage());
    }

    @Test
    void addBoldText_withEmptyText_createsParagraphWithBoldFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldText("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
    }

    @Test
    void addBoldText_withSpecialCharacters_createsParagraphWithBoldFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        String specialText = "bold <test> & text!";
        converter.addBoldText(specialText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals(specialText, paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
    }

    @Test
    void addBoldText_multipleBoldTexts_sequentially() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldText("first bold");
        converter.addBoldText("second bold");
        converter.addBoldText("third bold");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertTrue(runs.get(0).isBold());
        }

        assertEquals("first bold", paragraphs.get(0).getText());
        assertEquals("second bold", paragraphs.get(1).getText());
        assertEquals("third bold", paragraphs.get(2).getText());
    }

    @Test
    void convertStrongEmphasis_withSimpleText_createsParagraphWithBoldFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("**bold text**");
        StrongEmphasis strongEmphasis = (StrongEmphasis) doc.getFirstChild().getFirstChild();

        converter.convertStrongEmphasis(strongEmphasis);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("bold text", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
    }

    @Test
    void convertStrongEmphasis_withMultipleWords_createsParagraphWithBoldFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("**this is bold**");
        StrongEmphasis strongEmphasis = (StrongEmphasis) doc.getFirstChild().getFirstChild();

        converter.convertStrongEmphasis(strongEmphasis);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("this is bold", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
    }

    @Test
    void convertStrongEmphasis_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertStrongEmphasis(null)
        );

        assertEquals("StrongEmphasis cannot be null", exception.getMessage());
    }

    @Test
    void addItalicText_withSimpleText_createsParagraphWithItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addItalicText("italic text");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("italic text", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void addItalicText_withMultipleWords_createsParagraphWithItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addItalicText("this is italic text with multiple words");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("this is italic text with multiple words", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void addItalicText_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addItalicText(null)
        );

        assertEquals("Italic text cannot be null", exception.getMessage());
    }

    @Test
    void addItalicText_withEmptyText_createsParagraphWithItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addItalicText("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void addItalicText_withSpecialCharacters_createsParagraphWithItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        String specialText = "italic <test> & text!";
        converter.addItalicText(specialText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals(specialText, paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void addItalicText_multipleItalicTexts_sequentially() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addItalicText("first italic");
        converter.addItalicText("second italic");
        converter.addItalicText("third italic");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertTrue(runs.get(0).isItalic());
        }

        assertEquals("first italic", paragraphs.get(0).getText());
        assertEquals("second italic", paragraphs.get(1).getText());
        assertEquals("third italic", paragraphs.get(2).getText());
    }

    @Test
    void convertEmphasis_withSimpleText_createsParagraphWithItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("*italic text*");
        Emphasis emphasis = (Emphasis) doc.getFirstChild().getFirstChild();

        converter.convertEmphasis(emphasis);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("italic text", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void convertEmphasis_withMultipleWords_createsParagraphWithItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("*this is italic*");
        Emphasis emphasis = (Emphasis) doc.getFirstChild().getFirstChild();

        converter.convertEmphasis(emphasis);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("this is italic", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void convertEmphasis_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertEmphasis(null)
        );

        assertEquals("Emphasis cannot be null", exception.getMessage());
    }

    @Test
    void addBoldItalicText_withSimpleText_createsParagraphWithBoldAndItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldItalicText("bold italic text");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("bold italic text", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void addBoldItalicText_withMultipleWords_createsParagraphWithBoldAndItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldItalicText("this is bold and italic text");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("this is bold and italic text", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void addBoldItalicText_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addBoldItalicText(null)
        );

        assertEquals("Bold-italic text cannot be null", exception.getMessage());
    }

    @Test
    void addBoldItalicText_withEmptyText_createsParagraphWithBoldAndItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldItalicText("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void addBoldItalicText_withSpecialCharacters_createsParagraphWithBoldAndItalicFormatting() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        String specialText = "bold-italic <test> & text!";
        converter.addBoldItalicText(specialText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals(specialText, paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertTrue(runs.get(0).isBold());
        assertTrue(runs.get(0).isItalic());
    }

    @Test
    void savedDocument_reopened_boldFormattingIsPreserved() throws IOException {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldText("bold text");

        Path testFile = tempDir.resolve("test-bold.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(1, paragraphs.size());

            XWPFParagraph paragraph = paragraphs.get(0);
            assertEquals("bold text", paragraph.getText());

            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertTrue(runs.get(0).isBold());
        }
    }

    @Test
    void savedDocument_reopened_italicFormattingIsPreserved() throws IOException {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addItalicText("italic text");

        Path testFile = tempDir.resolve("test-italic.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(1, paragraphs.size());

            XWPFParagraph paragraph = paragraphs.get(0);
            assertEquals("italic text", paragraph.getText());

            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertTrue(runs.get(0).isItalic());
        }
    }

    @Test
    void savedDocument_reopened_boldItalicFormattingIsPreserved() throws IOException {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldItalicText("bold italic text");

        Path testFile = tempDir.resolve("test-bold-italic.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(1, paragraphs.size());

            XWPFParagraph paragraph = paragraphs.get(0);
            assertEquals("bold italic text", paragraph.getText());

            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertTrue(runs.get(0).isBold());
            assertTrue(runs.get(0).isItalic());
        }
    }

    @Test
    void convertDocument_withMultipleFormattings_convertsAllFormattings() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "**bold text**\n" +
            "*italic text*"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(2, paragraphs.size());

        XWPFParagraph firstParagraph = paragraphs.get(0);
        assertEquals("bold text", firstParagraph.getText());
        List<XWPFRun> firstRuns = firstParagraph.getRuns();
        assertEquals(1, firstRuns.size());
        assertTrue(firstRuns.get(0).isBold());

        XWPFParagraph secondParagraph = paragraphs.get(1);
        assertEquals("italic text", secondParagraph.getText());
        List<XWPFRun> secondRuns = secondParagraph.getRuns();
        assertEquals(1, secondRuns.size());
        assertTrue(secondRuns.get(0).isItalic());
    }

    @Test
    void convertDocument_withNoFormattings_createsNoParagraphs() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No formatting here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyFormattedText() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n" +
            "**Bold text**\n" +
            "More text here.\n" +
            "*Italic text*\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(2, paragraphs.size());

        XWPFParagraph firstParagraph = paragraphs.get(0);
        assertEquals("Bold text", firstParagraph.getText());
        List<XWPFRun> firstRuns = firstParagraph.getRuns();
        assertEquals(1, firstRuns.size());
        assertTrue(firstRuns.get(0).isBold());

        XWPFParagraph secondParagraph = paragraphs.get(1);
        assertEquals("Italic text", secondParagraph.getText());
        List<XWPFRun> secondRuns = secondParagraph.getRuns();
        assertEquals(1, secondRuns.size());
        assertTrue(secondRuns.get(0).isItalic());
    }

    @Test
    void boldText_notItalic_verifyOnlyBoldIsSet() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addBoldText("only bold");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        XWPFRun run = paragraphs.get(0).getRuns().get(0);

        assertTrue(run.isBold());
        assertFalse(run.isItalic());
    }

    @Test
    void italicText_notBold_verifyOnlyItalicIsSet() {
        document = new XWPFDocument();
        TextFormatterConverter converter = new TextFormatterConverter(document);

        converter.addItalicText("only italic");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        XWPFRun run = paragraphs.get(0).getRuns().get(0);

        assertFalse(run.isBold());
        assertTrue(run.isItalic());
    }
}
