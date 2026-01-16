package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.Code;
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
 * Unit tests for InlineCodeConverter.
 */
class InlineCodeConverterTest {

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
        InlineCodeConverter converter = new InlineCodeConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new InlineCodeConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addInlineCode_withSimpleText_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        converter.addInlineCode("code");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("code", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addInlineCode_withMultipleWords_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        converter.addInlineCode("multiple words code");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("multiple words code", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addInlineCode_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addInlineCode(null)
        );

        assertEquals("Inline code text cannot be null", exception.getMessage());
    }

    @Test
    void addInlineCode_withEmptyText_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        converter.addInlineCode("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addInlineCode_withSpecialCharacters_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        converter.addInlineCode("String str = \"hello\";");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("String str = \"hello\";", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addInlineCode_withMultipleInlineCodes_sequentially() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        converter.addInlineCode("first");
        converter.addInlineCode("second");
        converter.addInlineCode("third");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("first", paragraphs.get(0).getText());
        assertEquals("second", paragraphs.get(1).getText());
        assertEquals("third", paragraphs.get(2).getText());

        // Verify all have monospace font
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertEquals("Courier New", runs.get(0).getFontFamily());
        }
    }

    @Test
    void convertCode_withSimpleCode_createsMonospaceParagraph() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("`code`");
        Code code = (Code) doc.getFirstChild().getFirstChild();

        converter.convertCode(code);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("code", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void convertCode_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertCode(null)
        );

        assertEquals("Code cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMultipleCodes_convertsAllCodes() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "`first`\n" +
            "`second`\n" +
            "`third`"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("first", paragraphs.get(0).getText());
        assertEquals("second", paragraphs.get(1).getText());
        assertEquals("third", paragraphs.get(2).getText());

        // Verify all have monospace font
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertEquals("Courier New", runs.get(0).getFontFamily());
        }
    }

    @Test
    void convertDocument_withNoCodes_createsNoParagraphs() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No inline code here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyCodes() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n" +
            "`first code`\n" +
            "More text here.\n" +
            "`second code`\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(2, paragraphs.size());

        assertEquals("first code", paragraphs.get(0).getText());
        assertEquals("second code", paragraphs.get(1).getText());

        // Verify both have monospace font
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertEquals("Courier New", runs.get(0).getFontFamily());
        }
    }

    @Test
    void savedDocument_reopened_monospaceFormattingPersists() throws IOException {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        converter.addInlineCode("code");

        Path testFile = tempDir.resolve("test-inlinecode.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(1, paragraphs.size());

            XWPFParagraph paragraph = paragraphs.get(0);
            assertEquals("code", paragraph.getText());

            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertEquals("Courier New", runs.get(0).getFontFamily());
        }
    }

    @Test
    void monospaceFontVerification_checksFontFamilyIsCourierNew() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        converter.addInlineCode("test");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        XWPFParagraph paragraph = paragraphs.get(0);
        List<XWPFRun> runs = paragraph.getRuns();

        assertEquals(1, runs.size());
        XWPFRun run = runs.get(0);

        // Verify monospace font family
        String fontFamily = run.getFontFamily();
        assertNotNull(fontFamily);
        assertEquals("Courier New", fontFamily);
    }

    @Test
    void addInlineCode_withJavaCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        String javaCode = "public static void main(String[] args)";
        converter.addInlineCode(javaCode);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals(javaCode, paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addInlineCode_withHtmlCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        InlineCodeConverter converter = new InlineCodeConverter(document);

        String htmlCode = "<div class=\"container\">Hello</div>";
        converter.addInlineCode(htmlCode);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals(htmlCode, paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }
}
