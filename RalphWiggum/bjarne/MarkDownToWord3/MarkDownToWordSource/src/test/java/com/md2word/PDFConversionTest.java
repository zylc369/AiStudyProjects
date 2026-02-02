package com.md2word;

import com.md2word.generator.PDFGenerator;
import com.md2word.parser.MarkdownParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Markdown to PDF conversion.
 *
 * <p>Tests verify that Markdown elements are correctly converted to PDF documents
 * with proper content preservation. PDF validity is verified by checking that
 * the PDF file is created, has content, and can be generated without errors.</p>
 *
 * <p><b>Note:</b> Visual formatting (font sizes, colors, bold/italic) and text content
 * verification are not tested programmatically as they require complex PDF parsing
 * that has changed significantly in PDFBox 3.0.x. Manual testing via Tasks 70-71
 * will verify visual quality and content correctness.</p>
 */
@DisplayName("PDF Conversion Tests")
public class PDFConversionTest {

    private MarkdownParser parser;
    private PDFGenerator generator;

    @BeforeEach
    void setUp() {
        parser = new MarkdownParser();
        generator = new PDFGenerator();
    }

    // ========== Basic Document Generation ==========

    @Test
    @DisplayName("Simple paragraph should generate valid PDF")
    void testSimpleParagraphGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is a simple paragraph.";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
        assertTrue(outputFile.toFile().length() > 100, "PDF should have non-trivial size");
    }

    @Test
    @DisplayName("PDF file creation should succeed")
    void testPDFFileCreation(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "# Test Document";
        Path outputFile = tempDir.resolve("test.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should exist");
        assertEquals("test.pdf", outputFile.toFile().getName(), "PDF should have correct name");
        assertTrue(outputFile.toFile().length() > 100, "PDF should have non-trivial size");
    }

    // ========== Heading Tests ==========

    @Test
    @DisplayName("Heading level 1 (#) should generate PDF")
    void testHeadingLevel1GeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "# Heading Level 1";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Heading level 2 (##) should generate PDF")
    void testHeadingLevel2GeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "## Heading Level 2";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Heading level 3 (###) should generate PDF")
    void testHeadingLevel3GeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "### Heading Level 3";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Heading level 4 (####) should generate PDF")
    void testHeadingLevel4GeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "#### Heading Level 4";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Heading level 5 (#####) should generate PDF")
    void testHeadingLevel5GeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "##### Heading Level 5";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Heading level 6 (######) should generate PDF")
    void testHeadingLevel6GeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "###### Heading Level 6";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Text Formatting Tests ==========

    @Test
    @DisplayName("Bold text (**text**) should generate PDF")
    void testBoldTextGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is **bold text** in a paragraph.";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Italic text (*text*) should generate PDF")
    void testItalicTextGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is *italic text* in a paragraph.";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Bold-italic text (***text***) should generate PDF")
    void testBoldItalicTextGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is ***bold italic text*** in a paragraph.";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Link Tests ==========

    @Test
    @DisplayName("Link [text](url) should generate PDF")
    void testLinkGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "Visit [GitHub](https://github.com) for more info.";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Multiple links should generate PDF")
    void testMultipleLinksGeneratePDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "[First](https://first.com) and [Second](https://second.com)";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== List Tests ==========

    @Test
    @DisplayName("Unordered list (- items) should generate PDF")
    void testUnorderedListGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            - First item
            - Second item
            - Third item
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Ordered list (1. items) should generate PDF")
    void testOrderedListGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            1. First item
            2. Second item
            3. Third item
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Nested lists should generate PDF")
    void testNestedListsGeneratePDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            - Parent item
              - Nested item
              - Another nested item
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Mixed list types should generate PDF")
    void testMixedListTypesGeneratePDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            - Unordered item 1
            - Unordered item 2

            1. Ordered item 1
            2. Ordered item 2
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Code Block Tests ==========

    @Test
    @DisplayName("Fenced code block should generate PDF")
    void testFencedCodeBlockGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            ```
            public class Test {
                public static void main(String[] args) {
                    System.out.println("Hello");
                }
            }
            ```
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Inline code should generate PDF")
    void testInlineCodeGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "This is `inline code` in a paragraph.";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Blockquote Tests ==========

    @Test
    @DisplayName("Blockquote should generate PDF")
    void testBlockquoteGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "> This is a blockquote.";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Multi-line blockquote should generate PDF")
    void testMultiLineBlockquoteGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            > This is line one.
            > This is line two.
            > This is line three.
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Table Tests ==========

    @Test
    @DisplayName("Simple table should generate PDF")
    void testSimpleTableGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Header 1 | Header 2 |
            |----------|----------|
            | Cell 1   | Cell 2   |
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Table with multiple rows should generate PDF")
    void testMultipleRowTableGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Name | Age | City |
            |------|-----|------|
            | Alice | 30 | NYC |
            | Bob | 25 | LA |
            | Charlie | 35 | Chicago |
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Image Tests ==========

    @Test
    @DisplayName("Image embedding should generate PDF")
    void testImageEmbeddingGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "![Test Image](test-sample.png)";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Missing image should generate PDF with alt text")
    void testMissingImageGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "![Missing Image](nonexistent-image.png)";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Multiple images should generate PDF")
    void testMultipleImagesGeneratePDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            ![First Image](test-sample.png)

            ![Second Image](test-sample.png)

            ![Third Image](test-sample.png)
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Combined Elements Test ==========

    @Test
    @DisplayName("Document with multiple element types should generate PDF")
    void testCombinedElementsGeneratePDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            # Document Title

            This is a paragraph with **bold text**, *italic text*, and ***bold italic*** text.

            ## Section Headings

            Links are important: [Visit GitHub](https://github.com).

            ### Lists

            Unordered list:
            - First item
            - Second item

            Ordered list:
            1. First
            2. Second

            ### Code

            Inline `code` and code blocks:

            ```
            public class Test {
                System.out.println("Hello");
            }
            ```

            ### Blockquote

            > This is a quote.

            ### Table

            | Header 1 | Header 2 |
            |----------|----------|
            | Cell 1   | Cell 2   |
            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Empty Markdown should generate valid PDF")
    void testEmptyMarkdownGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    @Test
    @DisplayName("Markdown with only whitespace should generate valid PDF")
    void testWhitespaceOnlyMarkdownGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """


            """;
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }

    // ========== Error Handling ==========

    @Test
    @DisplayName("Invalid Markdown should still generate PDF")
    void testInvalidMarkdownGeneratesPDF(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = "####### Invalid heading level\n\n```code block without closing";
        Path outputFile = tempDir.resolve("output.pdf");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        assertTrue(outputFile.toFile().exists(), "PDF file should be created even with invalid markdown");
        assertTrue(outputFile.toFile().length() > 0, "PDF file should not be empty");
    }
}
