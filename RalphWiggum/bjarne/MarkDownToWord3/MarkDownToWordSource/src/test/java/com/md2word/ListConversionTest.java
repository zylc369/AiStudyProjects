package com.md2word;

import com.md2word.generator.WordGenerator;
import com.md2word.parser.MarkdownParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Markdown list conversion to Word documents.
 *
 * <p>Tests verify that Markdown lists (ordered and unordered, including nested lists) are correctly
 * converted to Word documents with proper list formatting and numbering.</p>
 */
@DisplayName("List Conversion Tests")
public class ListConversionTest {

    private MarkdownParser parser;
    private WordGenerator generator;

    @BeforeEach
    void setUp() {
        parser = new MarkdownParser();
        generator = new WordGenerator();
    }

    @Test
    @DisplayName("Simple unordered list should convert to bulleted list")
    void testSimpleUnorderedList(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            - First item
            - Second item
            - Third item
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();

            // Should have 3 list items with "List Bullet" style
            List<XWPFParagraph> listParagraphs = paragraphs.stream()
                    .filter(p -> "List Bullet".equals(p.getStyle()))
                    .toList();

            assertTrue(listParagraphs.size() >= 3, "Should have at least 3 list paragraphs with List Bullet style, found: " + listParagraphs.size());

            // Verify text content
            assertTrue(listParagraphs.get(0).getText().contains("First item"), "First item should exist");
            assertTrue(listParagraphs.get(1).getText().contains("Second item"), "Second item should exist");
            assertTrue(listParagraphs.get(2).getText().contains("Third item"), "Third item should exist");
        }
    }

    @Test
    @DisplayName("Unordered list with multiple items should convert correctly")
    void testUnorderedListMultipleItems(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            - Item 1
            - Item 2
            - Item 3
            - Item 4
            - Item 5
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            String fullText = paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (a, b) -> a + b);

            assertTrue(fullText.contains("Item 1"), "Should contain Item 1");
            assertTrue(fullText.contains("Item 2"), "Should contain Item 2");
            assertTrue(fullText.contains("Item 3"), "Should contain Item 3");
            assertTrue(fullText.contains("Item 4"), "Should contain Item 4");
            assertTrue(fullText.contains("Item 5"), "Should contain Item 5");
        }
    }

    @Test
    @DisplayName("Simple ordered list should convert to numbered list")
    void testSimpleOrderedList(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            1. First item
            2. Second item
            3. Third item
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();

            // Find numbered paragraphs with "List Number" style
            List<XWPFParagraph> numberedParagraphs = paragraphs.stream()
                    .filter(p -> "List Number".equals(p.getStyle()))
                    .toList();

            assertTrue(numberedParagraphs.size() >= 3, "Should have at least 3 numbered list paragraphs with List Number style, found: " + numberedParagraphs.size());

            // Verify text content
            assertTrue(numberedParagraphs.get(0).getText().contains("First item"), "First item should exist");
            assertTrue(numberedParagraphs.get(1).getText().contains("Second item"), "Second item should exist");
            assertTrue(numberedParagraphs.get(2).getText().contains("Third item"), "Third item should exist");
        }
    }

    @Test
    @DisplayName("Ordered list with multiple items should convert correctly")
    void testOrderedListMultipleItems(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            1. First step
            2. Second step
            3. Third step
            4. Fourth step
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            String fullText = paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (a, b) -> a + b);

            assertTrue(fullText.contains("First step"), "Should contain First step");
            assertTrue(fullText.contains("Second step"), "Should contain Second step");
            assertTrue(fullText.contains("Third step"), "Should contain Third step");
            assertTrue(fullText.contains("Fourth step"), "Should contain Fourth step");
        }
    }

    @Test
    @DisplayName("Nested unordered list should maintain hierarchy")
    void testNestedUnorderedList(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            - Level 1 item 1
              - Level 2 item 1.1
              - Level 2 item 1.2
            - Level 1 item 2
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            String fullText = paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (a, b) -> a + b);

            assertTrue(fullText.contains("Level 1 item 1"), "Should contain Level 1 item 1");
            assertTrue(fullText.contains("Level 2 item 1.1"), "Should contain Level 2 item 1.1");
            assertTrue(fullText.contains("Level 2 item 1.2"), "Should contain Level 2 item 1.2");
            assertTrue(fullText.contains("Level 1 item 2"), "Should contain Level 1 item 2");
        }
    }

    @Test
    @DisplayName("Nested ordered list should maintain hierarchy")
    void testNestedOrderedList(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            1. Level 1 item 1
               1. Level 2 item 1.1
               2. Level 2 item 1.2
            2. Level 1 item 2
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            String fullText = paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (a, b) -> a + b);

            assertTrue(fullText.contains("Level 1 item 1"), "Should contain Level 1 item 1");
            assertTrue(fullText.contains("Level 2 item 1.1"), "Should contain Level 2 item 1.1");
            assertTrue(fullText.contains("Level 2 item 1.2"), "Should contain Level 2 item 1.2");
            assertTrue(fullText.contains("Level 1 item 2"), "Should contain Level 1 item 2");
        }
    }

    @Test
    @DisplayName("Mixed nested list should maintain hierarchy")
    void testMixedNestedList(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            1. Ordered item 1
               - Unordered nested item
               - Another unordered item
            2. Ordered item 2
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            String fullText = paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (a, b) -> a + b);

            assertTrue(fullText.contains("Ordered item 1"), "Should contain Ordered item 1");
            assertTrue(fullText.contains("Unordered nested item"), "Should contain nested item");
            assertTrue(fullText.contains("Another unordered item"), "Should contain another nested item");
            assertTrue(fullText.contains("Ordered item 2"), "Should contain Ordered item 2");
        }
    }

    @Test
    @DisplayName("List with formatted text should preserve formatting")
    void testListWithFormattedText(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            - **Bold** item
            - *Italic* item
            - ***Bold italic*** item
            - Plain item
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        var document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            String fullText = paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .reduce("", (a, b) -> a + b);

            assertTrue(fullText.contains("Bold item"), "Should contain bold item");
            assertTrue(fullText.contains("Italic item"), "Should contain italic item");
            assertTrue(fullText.contains("Bold italic item"), "Should contain bold italic item");
            assertTrue(fullText.contains("Plain item"), "Should contain plain item");

            // Verify formatting is applied (check for runs with formatting)
            boolean hasBold = paragraphs.stream()
                    .flatMap(p -> p.getRuns().stream())
                    .anyMatch(run -> run.isBold() && run.getText(0).contains("Bold"));

            boolean hasItalic = paragraphs.stream()
                    .flatMap(p -> p.getRuns().stream())
                    .anyMatch(run -> run.isItalic() && run.getText(0).contains("Italic"));

            assertTrue(hasBold, "Should have bold formatting");
            assertTrue(hasItalic, "Should have italic formatting");
        }
    }
}
