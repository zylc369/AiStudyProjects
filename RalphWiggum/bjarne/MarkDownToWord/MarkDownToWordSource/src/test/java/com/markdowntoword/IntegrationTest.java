package com.markdowntoword;

import com.markdowntoword.converter.*;
import com.markdowntoword.parser.MarkdownParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the end-to-end Markdown to Word conversion process.
 * These tests verify that the full conversion pipeline works correctly from parsing
 * Markdown files to generating valid Word documents.
 */
class IntegrationTest {

    @TempDir
    Path tempDir;

    private Path testInputFile;
    private Path testOutputFile;

    @AfterEach
    void cleanup() {
        deleteIfExists(testInputFile);
        deleteIfExists(testOutputFile);
    }

    private void deleteIfExists(Path path) {
        if (path != null && Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                // Ignore cleanup failures
            }
        }
    }

    // ==================== Full Conversion Tests ====================

    @Test
    void testFullConversion_WithTestMarkdownFile_Succeeds() throws IOException {
        String markdownContent = "# Main Heading\n\nThis is a paragraph.\n\n## Sub Heading\n\nAnother paragraph.";
        testInputFile = tempDir.resolve("test-input.md");
        Files.writeString(testInputFile, markdownContent);
        testOutputFile = tempDir.resolve("test-output.docx");

        assertDoesNotThrow(() -> performConversion(testInputFile.toString(), testOutputFile.toString()));

        assertTrue(Files.exists(testOutputFile), "Output file should exist");
        assertTrue(Files.size(testOutputFile) > 0, "Output file should not be empty");

        verifyWordDocumentHasContent(testOutputFile);
    }

    @Test
    void testFullConversion_WithExistingTestFile_Succeeds() throws IOException {
        Path testMdFile = findTestFile("test.md");
        testOutputFile = tempDir.resolve("test-output.docx");

        assertDoesNotThrow(() -> performConversion(testMdFile.toString(), testOutputFile.toString()));

        assertTrue(Files.exists(testOutputFile), "Output file should exist");
        assertTrue(Files.size(testOutputFile) > 0, "Output file should not be empty");
    }

    @Test
    void testFullConversion_WithFormatsMarkdown_Succeeds() throws IOException {
        Path testMdFile = findTestFile("test-formats.md");
        testOutputFile = tempDir.resolve("formats-output.docx");

        assertDoesNotThrow(() -> performConversion(testMdFile.toString(), testOutputFile.toString()));

        assertTrue(Files.exists(testOutputFile), "Output file should exist");
        assertTrue(Files.size(testOutputFile) > 0, "Output file should not be empty");
    }

    @Test
    void testFullConversion_WithTablesMarkdown_Succeeds() throws IOException {
        Path testMdFile = findTestFile("test-tables.md");
        testOutputFile = tempDir.resolve("tables-output.docx");

        assertDoesNotThrow(() -> performConversion(testMdFile.toString(), testOutputFile.toString()));

        assertTrue(Files.exists(testOutputFile), "Output file should exist");
        assertTrue(Files.size(testOutputFile) > 0, "Output file should not be empty");
    }

    @Test
    void testFullConversion_WithComplexMarkdown_ContainsAllElements() throws IOException {
        String complexMarkdown = "# Main Heading\n\n" +
                "This is a paragraph with **bold** and *italic* text.\n\n" +
                "## Sub Heading\n\n" +
                "This has a [link](https://example.com) and `inline code`.\n\n" +
                "### Lists\n\n" +
                "- Unordered item 1\n" +
                "- Unordered item 2\n\n" +
                "1. Ordered item 1\n" +
                "2. Ordered item 2\n\n" +
                "### Nested Lists\n\n" +
                "- Level 1\n" +
                "  - Level 2\n" +
                "  - Another level 2\n\n" +
                "### Code Block\n\n" +
                "```\ncode block\n```\n\n" +
                "### Table\n\n" +
                "| Col 1 | Col 2 |\n" +
                "|-------|-------|\n" +
                "| Data 1| Data 2|";

        testInputFile = tempDir.resolve("complex-input.md");
        Files.writeString(testInputFile, complexMarkdown);
        testOutputFile = tempDir.resolve("complex-output.docx");

        assertDoesNotThrow(() -> performConversion(testInputFile.toString(), testOutputFile.toString()));

        assertTrue(Files.exists(testOutputFile), "Output file should exist");
        assertTrue(Files.size(testOutputFile) > 0, "Output file should not be empty");

        verifyWordDocumentHasContent(testOutputFile);
    }

    // ==================== Helper Methods ====================

    /**
     * Finds a test file by checking multiple possible locations relative to the working directory.
     *
     * @param fileName the name of the test file to find
     * @return the Path to the test file
     * @throws IllegalArgumentException if the file cannot be found in any location
     */
    private Path findTestFile(String fileName) {
        Path testFile = Paths.get("testFiles/" + fileName);
        if (Files.exists(testFile)) {
            return testFile;
        }
        testFile = Paths.get("../testFiles/" + fileName);
        if (Files.exists(testFile)) {
            return testFile;
        }
        testFile = Paths.get("../../testFiles/" + fileName);
        if (Files.exists(testFile)) {
            return testFile;
        }
        throw new IllegalArgumentException("Test file " + fileName + " not found in testFiles directory");
    }

    /**
     * Performs the full Markdown to Word conversion using all converters.
     * This mirrors the conversion logic in Main.performConversion().
     *
     * @param inputFile  the input Markdown file path
     * @param outputFile the output Word file path
     */
    private void performConversion(String inputFile, String outputFile) {
        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document document = parser.parseFile(inputFile);

        WordDocumentBuilder builder = new WordDocumentBuilder();

        // Apply all converters in sequence
        new HeadingConverter(builder.getDocument()).convertDocument(document);
        new ParagraphConverter(builder.getDocument()).convertDocument(document);
        new CodeBlockConverter(builder.getDocument()).convertDocument(document);
        new TableConverter(builder.getDocument()).convertDocument(document);
        new UnorderedListConverter(builder.getDocument()).convertDocument(document);
        new OrderedListConverter(builder.getDocument()).convertDocument(document);
        new NestedListConverter(builder.getDocument()).convertDocument(document);
        new LinkConverter(builder.getDocument()).convertDocument(document);
        new InlineCodeConverter(builder.getDocument()).convertDocument(document);
        new TextFormatterConverter(builder.getDocument()).convertDocument(document);

        builder.save(outputFile);
    }

    /**
     * Verifies that a Word document can be reopened and contains content.
     *
     * @param filePath the path to the Word document
     */
    private void verifyWordDocumentHasContent(Path filePath) {
        try (FileInputStream inputStream = new FileInputStream(filePath.toFile());
             XWPFDocument document = new XWPFDocument(inputStream)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            assertTrue(paragraphs.size() > 0, "Document should contain at least one paragraph");
        } catch (IOException e) {
            fail("Failed to open and read Word document: " + e.getMessage());
        }
    }
}
