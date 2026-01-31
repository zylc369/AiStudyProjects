package com.md2word;

import com.md2word.generator.WordGenerator;
import com.md2word.parser.MarkdownParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Markdown text formatting conversion to Word documents.
 *
 * <p>Tests verify that Markdown text formatting (bold, italic, bold-italic) is correctly
 * converted to Word documents with proper character formatting applied to runs.</p>
 */
@DisplayName("Text Formatting Tests")
public class TextFormattingTest {

    private MarkdownParser parser;
    private WordGenerator generator;

    @BeforeEach
    void setUp() {
        parser = new MarkdownParser();
        generator = new WordGenerator();
    }

    @Test
    @DisplayName("Bold text (**text**) should convert to bold runs")
    void testBoldText(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is **bold text** in a paragraph.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            assertTrue(runs.size() >= 2, "Paragraph should have at least 2 runs");

            // Find the bold run
            XWPFRun boldRun = runs.stream()
                    .filter(run -> run.getText(0).contains("bold text"))
                    .findFirst()
                    .orElseThrow();

            assertTrue(boldRun.isBold(), "Bold text run should have isBold() == true");
            assertEquals("bold text", boldRun.getText(0), "Bold text content should be preserved");
        }
    }

    @Test
    @DisplayName("Bold text (__text__) with alternative syntax should convert to bold runs")
    void testBoldTextAltSyntax(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is __bold text__ in a paragraph.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            // Find the bold run
            XWPFRun boldRun = runs.stream()
                    .filter(run -> run.getText(0) != null && run.getText(0).contains("bold text"))
                    .findFirst()
                    .orElseThrow();

            assertTrue(boldRun.isBold(), "Bold text run should have isBold() == true");
        }
    }

    @Test
    @DisplayName("Italic text (*text*) should convert to italic runs")
    void testItalicText(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is *italic text* in a paragraph.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            assertTrue(runs.size() >= 2, "Paragraph should have at least 2 runs");

            // Find the italic run
            XWPFRun italicRun = runs.stream()
                    .filter(run -> run.getText(0).contains("italic text"))
                    .findFirst()
                    .orElseThrow();

            assertTrue(italicRun.isItalic(), "Italic text run should have isItalic() == true");
            assertEquals("italic text", italicRun.getText(0), "Italic text content should be preserved");
        }
    }

    @Test
    @DisplayName("Italic text (_text_) with alternative syntax should convert to italic runs")
    void testItalicTextAltSyntax(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is _italic text_ in a paragraph.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            // Find the italic run
            XWPFRun italicRun = runs.stream()
                    .filter(run -> run.getText(0) != null && run.getText(0).contains("italic text"))
                    .findFirst()
                    .orElseThrow();

            assertTrue(italicRun.isItalic(), "Italic text run should have isItalic() == true");
        }
    }

    @Test
    @DisplayName("Bold-italic text (***text***) should convert to bold+italic runs")
    void testBoldItalicText(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is ***bold italic text*** in a paragraph.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            assertTrue(runs.size() >= 2, "Paragraph should have at least 2 runs");

            // Find the bold-italic run
            XWPFRun boldItalicRun = runs.stream()
                    .filter(run -> run.getText(0).contains("bold italic text"))
                    .findFirst()
                    .orElseThrow();

            assertTrue(boldItalicRun.isBold(), "Bold-italic text run should have isBold() == true");
            assertTrue(boldItalicRun.isItalic(), "Bold-italic text run should have isItalic() == true");
            assertEquals("bold italic text", boldItalicRun.getText(0), "Bold-italic text content should be preserved");
        }
    }

    @Test
    @DisplayName("Bold-italic text (___text___) with alternative syntax should convert to bold+italic runs")
    void testBoldItalicTextAltSyntax(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is ___bold italic text___ in a paragraph.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            // Find the bold-italic run
            XWPFRun boldItalicRun = runs.stream()
                    .filter(run -> run.getText(0) != null && run.getText(0).contains("bold italic text"))
                    .findFirst()
                    .orElseThrow();

            assertTrue(boldItalicRun.isBold(), "Bold-italic text run should have isBold() == true");
            assertTrue(boldItalicRun.isItalic(), "Bold-italic text run should have isItalic() == true");
        }
    }

    @Test
    @DisplayName("Multiple formatting types in one paragraph should convert correctly")
    void testMultipleFormattingInParagraph(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This has **bold**, *italic*, and ***bold italic*** text.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            // Should have multiple runs for different formatted sections
            assertTrue(runs.size() >= 4, "Paragraph should have at least 4 runs for mixed formatting");

            // Verify we have bold, italic, and bold-italic runs
            boolean hasBold = runs.stream().anyMatch(XWPFRun::isBold);
            boolean hasItalic = runs.stream().anyMatch(XWPFRun::isItalic);

            assertTrue(hasBold, "Should have at least one bold run");
            assertTrue(hasItalic, "Should have at least one italic run");
        }
    }

    @Test
    @DisplayName("Plain text should have no formatting applied")
    void testPlainText(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is plain text with no formatting.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            List<XWPFRun> runs = paragraph.getRuns();

            assertEquals(1, runs.size(), "Plain text paragraph should have 1 run");
            XWPFRun run = runs.get(0);

            assertFalse(run.isBold(), "Plain text should not be bold");
            assertFalse(run.isItalic(), "Plain text should not be italic");
            assertEquals("This is plain text with no formatting.", run.getText(0), "Plain text content should be preserved");
        }
    }

    @Test
    @DisplayName("Mixed content with plain, bold, and italic text should convert correctly")
    void testMixedFormatting(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "Normal text **bold part** more normal *italic part** and done.";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);

            // Verify paragraph text is preserved
            String fullText = paragraph.getText();
            assertTrue(fullText.contains("Normal text"), "Should contain 'Normal text'");
            assertTrue(fullText.contains("bold part"), "Should contain 'bold part'");
            assertTrue(fullText.contains("more normal"), "Should contain 'more normal'");
            assertTrue(fullText.contains("italic part"), "Should contain 'italic part'");
            assertTrue(fullText.contains("and done"), "Should contain 'and done'");

            // Verify formatting exists
            List<XWPFRun> runs = paragraph.getRuns();
            assertTrue(runs.size() > 1, "Mixed content should create multiple runs");

            boolean hasBold = runs.stream().anyMatch(XWPFRun::isBold);
            boolean hasItalic = runs.stream().anyMatch(XWPFRun::isItalic);

            assertTrue(hasBold, "Should have bold formatting");
            assertTrue(hasItalic, "Should have italic formatting");
        }
    }
}
