package com.md2word;

import com.md2word.generator.WordGenerator;
import com.md2word.parser.MarkdownParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Markdown heading conversion to Word documents.
 *
 * <p>Tests verify that Markdown headings (# through ######) are correctly
 * converted to Word documents with proper heading styles and text content.</p>
 */
@DisplayName("Heading Conversion Tests")
public class HeadingConversionTest {

    private MarkdownParser parser;
    private WordGenerator generator;

    @BeforeEach
    void setUp() {
        parser = new MarkdownParser();
        generator = new WordGenerator();
    }

    @Test
    @DisplayName("Heading level 1 (#) should convert to Heading1 style")
    void testHeadingLevel1(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "# Heading Level 1";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            assertEquals("Heading1", paragraph.getStyle(), "Paragraph should have Heading1 style");
            assertEquals("Heading Level 1", paragraph.getText(), "Paragraph text should match heading text");
        }
    }

    @Test
    @DisplayName("Heading level 2 (##) should convert to Heading2 style")
    void testHeadingLevel2(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "## Heading Level 2";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            assertEquals("Heading2", paragraph.getStyle(), "Paragraph should have Heading2 style");
            assertEquals("Heading Level 2", paragraph.getText(), "Paragraph text should match heading text");
        }
    }

    @Test
    @DisplayName("Heading level 3 (###) should convert to Heading3 style")
    void testHeadingLevel3(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "### Heading Level 3";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            assertEquals("Heading3", paragraph.getStyle(), "Paragraph should have Heading3 style");
            assertEquals("Heading Level 3", paragraph.getText(), "Paragraph text should match heading text");
        }
    }

    @Test
    @DisplayName("Heading level 4 (####) should convert to Heading4 style")
    void testHeadingLevel4(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "#### Heading Level 4";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            assertEquals("Heading4", paragraph.getStyle(), "Paragraph should have Heading4 style");
            assertEquals("Heading Level 4", paragraph.getText(), "Paragraph text should match heading text");
        }
    }

    @Test
    @DisplayName("Heading level 5 (#####) should convert to Heading5 style")
    void testHeadingLevel5(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "##### Heading Level 5";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            assertEquals("Heading5", paragraph.getStyle(), "Paragraph should have Heading5 style");
            assertEquals("Heading Level 5", paragraph.getText(), "Paragraph text should match heading text");
        }
    }

    @Test
    @DisplayName("Heading level 6 (######) should convert to Heading6 style")
    void testHeadingLevel6(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "###### Heading Level 6";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            assertEquals("Heading6", paragraph.getStyle(), "Paragraph should have Heading6 style");
            assertEquals("Heading Level 6", paragraph.getText(), "Paragraph text should match heading text");
        }
    }

    @Test
    @DisplayName("Heading text content should be preserved correctly")
    void testHeadingTextContent(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "# This is a heading with special characters: 123, !@#, and UTF-8: 你好";
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(1, doc.getParagraphs().size(), "Document should have 1 paragraph");
            XWPFParagraph paragraph = doc.getParagraphs().get(0);
            assertEquals("This is a heading with special characters: 123, !@#, and UTF-8: 你好",
                        paragraph.getText(),
                        "Paragraph text should preserve all characters");
        }
    }

    @Test
    @DisplayName("Multiple headings in document should all convert correctly")
    void testMultipleHeadings(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            # First Heading
            ## Second Heading
            ### Third Heading
            #### Fourth Heading
            ##### Fifth Heading
            ###### Sixth Heading
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertEquals(6, doc.getParagraphs().size(), "Document should have 6 paragraphs");

            assertEquals("Heading1", doc.getParagraphs().get(0).getStyle());
            assertEquals("First Heading", doc.getParagraphs().get(0).getText());

            assertEquals("Heading2", doc.getParagraphs().get(1).getStyle());
            assertEquals("Second Heading", doc.getParagraphs().get(1).getText());

            assertEquals("Heading3", doc.getParagraphs().get(2).getStyle());
            assertEquals("Third Heading", doc.getParagraphs().get(2).getText());

            assertEquals("Heading4", doc.getParagraphs().get(3).getStyle());
            assertEquals("Fourth Heading", doc.getParagraphs().get(3).getText());

            assertEquals("Heading5", doc.getParagraphs().get(4).getStyle());
            assertEquals("Fifth Heading", doc.getParagraphs().get(4).getText());

            assertEquals("Heading6", doc.getParagraphs().get(5).getStyle());
            assertEquals("Sixth Heading", doc.getParagraphs().get(5).getText());
        }
    }
}
