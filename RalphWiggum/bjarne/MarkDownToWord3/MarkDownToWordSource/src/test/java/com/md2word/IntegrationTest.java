package com.md2word;

import com.md2word.generator.WordGenerator;
import com.md2word.parser.MarkdownParser;
import com.vladsch.flexmark.util.ast.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for complete Markdown to Word document conversion.
 *
 * <p>These tests verify end-to-end conversion of complete Markdown documents
 * with all supported elements to ensure no content loss and proper formatting preservation.</p>
 */
@DisplayName("Integration Tests - Full Document Conversion")
public class IntegrationTest {

    private MarkdownParser parser;
    private WordGenerator generator;

    @BeforeEach
    void setUp() {
        parser = new MarkdownParser();
        generator = new WordGenerator();
    }

    @Test
    @DisplayName("Full document conversion should preserve all content elements")
    void testFullDocumentConversion(@TempDir Path tempDir) throws Exception {
        // Arrange: Comprehensive Markdown with all supported elements
        String markdown = """
            # Document Title

            This is a paragraph with **bold text**, *italic text*, and ***bold italic*** text.

            ## Section Headings

            ### Subsection

            #### Sub-subsection

            Links are important: [Visit GitHub](https://github.com) and [Markdown Guide](https://www.markdownguide.org/).

            ### Lists

            Unordered list:
            - First item
            - Second item
              - Nested item
              - Another nested item
            - Third item

            Ordered list:
            1. First step
            2. Second step
            3. Third step

            ### Tables

            | Name | Age | City |
            |------|-----|------|
            | Alice | 30 | New York |
            | Bob | 25 | London |
            | **Charlie** | 35 | *Paris* |

            ### Code Blocks

            Inline `code` example.

            Fenced code block:
            ```
            public class Example {
                public static void main(String[] args) {
                    System.out.println("Hello");
                }
            }
            ```

            ### Blockquotes

            > This is a blockquote.
            > It can span multiple lines.

            ### Horizontal Rule

            ---

            ### Text Formatting Examples

            - **Bold**: This is **bold text**
            - *Italic*: This is *italic text*
            - ***Bold Italic***: This is ***bold and italic***

            ## Final Section

            The document ends here with a paragraph containing a [link](https://example.com) and `inline code`.
            """;

        Path outputFile = tempDir.resolve("full-document-output.docx");

        // Act: Parse and generate Word document
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert: Verify all content elements are preserved

        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {

            // Verify document has content
            assertFalse(doc.getParagraphs().isEmpty(), "Document should have paragraphs");
            assertFalse(doc.getTables().isEmpty(), "Document should have tables");

            // Check headings
            long headingCount = doc.getParagraphs().stream()
                    .filter(p -> p.getStyle() != null && p.getStyle().startsWith("Heading"))
                    .count();
            assertTrue(headingCount >= 6, "Document should have at least 6 headings (found: " + headingCount + ")");

            // Verify heading levels are present
            assertTrue(hasHeadingWithStyle(doc, "Heading1"), "Should have Heading1");
            assertTrue(hasHeadingWithStyle(doc, "Heading2"), "Should have Heading2");
            assertTrue(hasHeadingWithStyle(doc, "Heading3"), "Should have Heading3");
            assertTrue(hasHeadingWithStyle(doc, "Heading4"), "Should have Heading4");

            // Check for bold formatting
            boolean hasBold = doc.getParagraphs().stream()
                    .flatMap(p -> p.getRuns().stream())
                    .anyMatch(XWPFRun::isBold);
            assertTrue(hasBold, "Document should contain bold text");

            // Check for italic formatting
            boolean hasItalic = doc.getParagraphs().stream()
                    .flatMap(p -> p.getRuns().stream())
                    .anyMatch(XWPFRun::isItalic);
            assertTrue(hasItalic, "Document should contain italic text");

            // Check for hyperlinks (blue underlined text)
            boolean hasLinks = doc.getParagraphs().stream()
                    .flatMap(p -> p.getRuns().stream())
                    .anyMatch(run -> run.getUnderline() != null &&
                                 run.getColor() != null &&
                                 run.getColor().equals("0000FF"));
            assertTrue(hasLinks, "Document should contain hyperlinks");

            // Check for list items
            long listCount = doc.getParagraphs().stream()
                    .filter(p -> p.getStyle() != null && p.getStyle().startsWith("List"))
                    .count();
            assertTrue(listCount >= 6, "Document should have list items (found: " + listCount + ")");

            // Check for tables
            assertTrue(doc.getTables().size() >= 1, "Document should have at least 1 table");
            XWPFTable table = doc.getTables().get(0);
            assertTrue(table.getRows().size() >= 3, "Table should have header + 2 data rows");
            assertTrue(table.getRow(0).getTableCells().size() >= 3, "Table should have at least 3 columns");

            // Check table content
            // Extract text from both paragraphs and table cells
            StringBuilder fullTextBuilder = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) {
                fullTextBuilder.append(para.getText()).append(" ");
            }
            for (XWPFTable tbl : doc.getTables()) {
                for (var row : tbl.getRows()) {
                    for (var cell : row.getTableCells()) {
                        fullTextBuilder.append(cell.getText()).append(" ");
                    }
                }
            }
            String fullText = fullTextBuilder.toString();

            assertTrue(fullText.contains("Alice"), "Table content should be preserved");
            assertTrue(fullText.contains("Bob"), "Table content should be preserved");
            assertTrue(fullText.contains("Charlie"), "Table content should be preserved");

            // Check for code blocks (monospace font)
            boolean hasMonospace = doc.getParagraphs().stream()
                    .flatMap(p -> p.getRuns().stream())
                    .anyMatch(run -> run.getFontFamily() != null &&
                                 run.getFontFamily().contains("Courier"));
            assertTrue(hasMonospace, "Document should contain monospace code text");

            // Check for inline code
            assertTrue(fullText.contains("code") || fullText.contains("code example"),
                       "Document should contain inline code");

            // Check for blockquotes (indentation)
            boolean hasBlockquote = doc.getParagraphs().stream()
                    .anyMatch(p -> p.getIndentationLeft() > 0);
            assertTrue(hasBlockquote, "Document should contain blockquote with indentation");

            // Check for horizontal rules (borders)
            boolean hasHorizontalRule = doc.getParagraphs().stream()
                    .anyMatch(p -> p.getBorderBottom() != null);
            assertTrue(hasHorizontalRule, "Document should contain horizontal rule");

            // Verify specific content is preserved
            assertTrue(fullText.contains("Document Title"), "Title should be preserved");
            assertTrue(fullText.contains("GitHub"), "Link text should be preserved");
            assertTrue(fullText.contains("First item"), "List items should be preserved");
            assertTrue(fullText.contains("public class Example"), "Code block content should be preserved");
            assertTrue(fullText.contains("blockquote"), "Blockquote content should be preserved");
        }
    }

    @Test
    @DisplayName("Integration test with minimal document should convert successfully")
    void testMinimalDocumentConversion(@TempDir Path tempDir) throws Exception {
        // Arrange: Simple but complete document
        String markdown = """
            # Simple Document

            This is a **test** with *formatting*.

            ## Section

            - Item 1
            - Item 2

            [Link](https://example.com)
            """;

        Path outputFile = tempDir.resolve("minimal-output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert: Basic sanity checks
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            assertFalse(doc.getParagraphs().isEmpty(), "Document should have content");

            String fullText = doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (a, b) -> a + " " + b);

            assertTrue(fullText.contains("Simple Document"), "Title should be preserved");
            assertTrue(fullText.contains("test"), "Content should be preserved");
            assertTrue(fullText.contains("Item 1"), "List should be preserved");
        }
    }

    /**
     * Helper method to check if document has a paragraph with specific heading style.
     */
    private boolean hasHeadingWithStyle(XWPFDocument doc, String style) {
        return doc.getParagraphs().stream()
                .anyMatch(p -> style.equals(p.getStyle()));
    }
}
