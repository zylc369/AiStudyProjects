package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
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
 * Unit tests for CodeBlockConverter.
 */
class CodeBlockConverterTest {

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
        CodeBlockConverter converter = new CodeBlockConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CodeBlockConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addCodeBlock_withSimpleCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("code");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("code", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addCodeBlock_withMultilineCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        String multilineCode = "line one\nline two\nline three";
        converter.addCodeBlock(multilineCode);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        // getText() returns all text in paragraph with line breaks preserved
        assertEquals(multilineCode, paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(3, runs.size());

        // Verify monospace font on all runs
        for (XWPFRun run : runs) {
            assertEquals("Courier New", run.getFontFamily());
        }

        // Verify line breaks are present in runs after the first
        assertTrue(runs.get(1).getCTR().getBrList().size() > 0);
        assertTrue(runs.get(2).getCTR().getBrList().size() > 0);
    }

    @Test
    void addCodeBlock_withEmptyCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addCodeBlock_withSpecialCharacters_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        String specialCharsCode = "String str = \"hello\";\nint x = 42;";
        converter.addCodeBlock(specialCharsCode);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);

        List<XWPFRun> runs = paragraph.getRuns();
        assertTrue(runs.size() >= 1);

        // Verify monospace font on all runs
        for (XWPFRun run : runs) {
            assertEquals("Courier New", run.getFontFamily());
        }
    }

    @Test
    void addCodeBlock_withNullCode_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addCodeBlock(null)
        );

        assertEquals("Code content cannot be null", exception.getMessage());
    }

    @Test
    void addCodeBlock_withSingleLineCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("public void main() { }");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("public void main() { }", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void addCodeBlock_withMultipleCodeBlocks_sequentially() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("first");
        converter.addCodeBlock("second");
        converter.addCodeBlock("third");

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
    void convertFencedCodeBlock_withSimpleCode_createsMonospaceParagraph() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("```\ncode\n```");
        FencedCodeBlock block = (FencedCodeBlock) doc.getFirstChild();

        converter.convertFencedCodeBlock(block);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("code", paragraph.getText());

        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(1, runs.size());
        assertEquals("Courier New", runs.get(0).getFontFamily());
    }

    @Test
    void convertFencedCodeBlock_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertFencedCodeBlock(null)
        );

        assertEquals("FencedCodeBlock cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMultipleCodeBlocks_convertsAllCodeBlocks() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "```\nfirst\n```\n" +
            "```\nsecond\n```\n" +
            "```\nthird\n```"
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
    void convertDocument_withNoCodeBlocks_createsNoParagraphs() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No code blocks here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyCodeBlocks() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n" +
            "```\nfirst code\n```\n" +
            "More text here.\n" +
            "```\nsecond code\n```\n" +
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
    void monospaceFontVerification_checksFontFamilyIsCourierNew() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("test");

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
    void savedDocument_reopened_monospaceFormattingPersists() throws IOException {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("code");

        Path testFile = tempDir.resolve("test-codeblock.docx");
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
    void addCodeBlock_withJavaCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        String javaCode = "public static void main(String[] args) {\n" +
                          "    System.out.println(\"Hello\");\n" +
                          "}";
        converter.addCodeBlock(javaCode);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        List<XWPFRun> runs = paragraphs.get(0).getRuns();
        assertTrue(runs.size() >= 1);

        // Verify monospace font on all runs
        for (XWPFRun run : runs) {
            assertEquals("Courier New", run.getFontFamily());
        }
    }

    @Test
    void addCodeBlock_withHtmlCode_createsParagraphWithMonospaceFont() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        String htmlCode = "<div class=\"container\">\n" +
                         "  <h1>Hello</h1>\n" +
                         "  <p>World</p>\n" +
                         "</div>";
        converter.addCodeBlock(htmlCode);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        List<XWPFRun> runs = paragraphs.get(0).getRuns();
        assertTrue(runs.size() >= 1);

        // Verify monospace font on all runs
        for (XWPFRun run : runs) {
            assertEquals("Courier New", run.getFontFamily());
        }
    }

    @Test
    void codeBlockHasBackgroundShading() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("test code");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);

        // Verify background shading is set
        CTPPr pPr = paragraph.getCTP().getPPr();
        assertNotNull(pPr, "Paragraph properties should not be null");

        CTShd shd = pPr.getShd();
        assertNotNull(shd, "Background shading should be set");
        assertNotNull(shd.getFill(), "Background fill color should be set");

        // Verify the fill value represents a light gray color
        // The fill can be returned as either "EEEEEE" or as a byte array representation
        Object fillValue = shd.getFill();
        if (fillValue instanceof String) {
            assertEquals("EEEEEE", fillValue, "Background color should be EEEEEE (light gray)");
        } else if (fillValue instanceof byte[]) {
            byte[] bytes = (byte[]) fillValue;
            // Check that it's a light gray color (RGB: 238, 238, 238)
            assertEquals(3, bytes.length);
            assertEquals(-18, bytes[0]); // 0xEE = 238, as signed byte = -18
            assertEquals(-18, bytes[1]);
            assertEquals(-18, bytes[2]);
        }
    }

    @Test
    void codeBlockHasSpacingBeforeAndAfter() {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("test code");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);

        // Verify spacing before and after are set
        assertEquals(100, paragraph.getSpacingBefore(), "Spacing before should be 100 points");
        assertEquals(100, paragraph.getSpacingAfter(), "Spacing after should be 100 points");
    }

    @Test
    void formattingPersistsAfterSave() throws IOException {
        document = new XWPFDocument();
        CodeBlockConverter converter = new CodeBlockConverter(document);

        converter.addCodeBlock("test code");

        Path testFile = tempDir.resolve("test-formatting.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(1, paragraphs.size());

            XWPFParagraph paragraph = paragraphs.get(0);

            // Verify background shading persists
            CTPPr pPr = paragraph.getCTP().getPPr();
            assertNotNull(pPr, "Paragraph properties should persist after save");
            CTShd shd = pPr.getShd();
            assertNotNull(shd, "Background shading should persist after save");
            assertNotNull(shd.getFill(), "Background fill should persist after save");

            // Verify the fill value represents a light gray color
            // The fill can be returned as either "EEEEEE" or as a byte array representation
            Object fillValue = shd.getFill();
            if (fillValue instanceof String) {
                assertEquals("EEEEEE", fillValue, "Background color should persist as EEEEEE");
            } else if (fillValue instanceof byte[]) {
                byte[] bytes = (byte[]) fillValue;
                // Check that it's a light gray color (RGB: 238, 238, 238)
                assertEquals(3, bytes.length);
                assertEquals(-18, bytes[0]); // 0xEE = 238, as signed byte = -18
                assertEquals(-18, bytes[1]);
                assertEquals(-18, bytes[2]);
            }

            // Verify spacing persists
            assertEquals(100, paragraph.getSpacingBefore(), "Spacing before should persist after save");
            assertEquals(100, paragraph.getSpacingAfter(), "Spacing after should persist after save");

            // Verify monospace font persists
            List<XWPFRun> runs = paragraph.getRuns();
            assertEquals(1, runs.size());
            assertEquals("Courier New", runs.get(0).getFontFamily());
        }
    }
}
