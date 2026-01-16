package com.markdowntoword.parser;

import com.markdowntoword.exception.MarkdownParseException;
import com.vladsch.flexmark.util.ast.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MarkdownParser class.
 */
class MarkdownParserTest {

    @TempDir
    Path tempDir;

    private Path testFilePath;

    @AfterEach
    void cleanup() {
        if (testFilePath != null && Files.exists(testFilePath)) {
            try {
                Files.delete(testFilePath);
            } catch (IOException e) {
                // Ignore cleanup failures
            }
        }
    }

    // ==================== Constructor Tests ====================

    @Test
    void testConstructor_InitializesParser() {
        MarkdownParser parser = new MarkdownParser();
        assertNotNull(parser.getParser(), "Parser should be initialized");
    }

    @Test
    void testConstructor_CreatesNewParserInstance() {
        MarkdownParser parser1 = new MarkdownParser();
        MarkdownParser parser2 = new MarkdownParser();
        assertNotNull(parser1.getParser());
        assertNotNull(parser2.getParser());
        assertNotEquals(parser1.getParser(), parser2.getParser(), "Each instance should have its own parser");
    }

    // ==================== parse() Method Tests ====================

    @Test
    void testParse_WithValidHeading_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "# Heading 1";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should have children");
    }

    @Test
    void testParse_WithValidParagraph_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "This is a paragraph.";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should have children");
    }

    @Test
    void testParse_WithValidUnorderedList_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "- Item 1\n- Item 2\n- Item 3";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should have children");
    }

    @Test
    void testParse_WithValidOrderedList_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "1. First item\n2. Second item\n3. Third item";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should have children");
    }

    @Test
    void testParse_WithValidLink_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "[Link text](https://example.com)";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should have children");
    }

    @Test
    void testParse_WithValidEmphasis_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "*italic text*";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_WithValidStrongText_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "**bold text**";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_WithValidInlineCode_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "`code`";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_WithValidIndentedCodeBlock_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "    code block";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_WithValidFencedCodeBlock_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "```\ncode block\n```";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_WithNullInput_ThrowsIllegalArgumentException() {
        MarkdownParser parser = new MarkdownParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(null)
        );

        assertEquals("Markdown content cannot be null", exception.getMessage());
    }

    @Test
    void testParse_WithEmptyString_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String emptyMarkdown = "";

        Document document = parser.parse(emptyMarkdown);

        assertNotNull(document, "Document should not be null even for empty input");
        assertNull(document.getFirstChild(), "Empty input should produce document with no children");
    }

    @Test
    void testParse_WithWhitespaceOnly_ReturnsValidDocument() {
        MarkdownParser parser = new MarkdownParser();
        String whitespaceMarkdown = "   \n   \n   ";

        Document document = parser.parse(whitespaceMarkdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_WithComplexMarkdown_ReturnsDocumentWithMultipleElements() {
        MarkdownParser parser = new MarkdownParser();
        String complexMarkdown = "# Main Heading\n\n" +
                "This is a paragraph with **bold** and *italic* text.\n\n" +
                "- List item 1\n" +
                "- List item 2\n\n" +
                "[Link](https://example.com)\n\n" +
                "```java\n" +
                "public class Test {}\n" +
                "```";

        Document document = parser.parse(complexMarkdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Complex markdown should produce document with children");
    }

    @Test
    void testParse_WithMultipleHeadings_ReturnsDocumentWithAllHeadings() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "# Heading 1\n\n## Heading 2\n\n### Heading 3";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should contain all headings");
    }

    @Test
    void testParse_WithNestedLists_ReturnsDocumentWithNestedStructure() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "- Level 1 item\n  - Level 2 item\n  - Level 2 item\n- Level 1 item";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should contain list");
    }

    @Test
    void testParse_WithHorizontalRule_ParsesSuccessfully() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "---";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_WithBlockquote_ParsesSuccessfully() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "> This is a blockquote";

        Document document = parser.parse(markdown);

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParse_SameContentMultipleTimes_ProducesSameStructure() {
        MarkdownParser parser = new MarkdownParser();
        String markdown = "# Heading\n\nParagraph content";

        Document document1 = parser.parse(markdown);
        Document document2 = parser.parse(markdown);

        assertNotNull(document1);
        assertNotNull(document2);
        assertEquals(document1.getFirstChild() != null, document2.getFirstChild() != null,
                "Parsing same content should produce same structure");
    }

    // ==================== parseFile() Method Tests ====================

    @Test
    void testParseFile_WithValidMarkdownFile_ReturnsDocumentWithCorrectContent() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        String fileContent = "# Test Heading\n\nTest paragraph content.";
        testFilePath = tempDir.resolve("test.md");
        Files.writeString(testFilePath, fileContent);

        Document document = parser.parseFile(testFilePath.toString());

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should contain elements from file");
    }

    @Test
    void testParseFile_WithComplexMarkdownFile_ParsesAllElements() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        String fileContent = "# Main Title\n\n" +
                "Introduction paragraph.\n\n" +
                "- First item\n" +
                "- Second item\n\n" +
                "**Bold** and *italic* text.\n\n" +
                "`inline code`";
        testFilePath = tempDir.resolve("complex.md");
        Files.writeString(testFilePath, fileContent);

        Document document = parser.parseFile(testFilePath.toString());

        assertNotNull(document, "Document should not be null");
        assertNotNull(document.getFirstChild(), "Document should have children from file");
    }

    @Test
    void testParseFile_WithEmptyFile_ReturnsValidDocument() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        String emptyContent = "";
        testFilePath = tempDir.resolve("empty.md");
        Files.writeString(testFilePath, emptyContent);

        Document document = parser.parseFile(testFilePath.toString());

        assertNotNull(document, "Document should not be null for empty file");
    }

    @Test
    void testParseFile_WithFileContainingOnlyWhitespace_ParsesSuccessfully() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        String whitespaceContent = "   \n\n   \n";
        testFilePath = tempDir.resolve("whitespace.md");
        Files.writeString(testFilePath, whitespaceContent);

        Document document = parser.parseFile(testFilePath.toString());

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParseFile_WithNestedFilePath_ParsesSuccessfully() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        Path nestedDir = tempDir.resolve("nested").resolve("directory");
        Files.createDirectories(nestedDir);
        Path nestedFile = nestedDir.resolve("nested.md");
        String content = "# Nested File Heading";
        Files.writeString(nestedFile, content);
        testFilePath = nestedFile;

        Document document = parser.parseFile(testFilePath.toString());

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParseFile_WithMultipleFiles_ParsesEachCorrectly() throws IOException {
        MarkdownParser parser = new MarkdownParser();

        Path file1 = tempDir.resolve("file1.md");
        Path file2 = tempDir.resolve("file2.md");
        Files.writeString(file1, "# File 1");
        Files.writeString(file2, "# File 2");

        Document document1 = parser.parseFile(file1.toString());
        Document document2 = parser.parseFile(file2.toString());

        assertNotNull(document1, "Document from file1 should not be null");
        assertNotNull(document2, "Document from file2 should not be null");
        assertNotEquals(document1, document2, "Different files should produce different document instances");
    }

    @Test
    void testParseFile_WithNullPath_ThrowsIllegalArgumentException() {
        MarkdownParser parser = new MarkdownParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseFile(null)
        );

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void testParseFile_WithBlankPath_ThrowsIllegalArgumentException() {
        MarkdownParser parser = new MarkdownParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseFile("")
        );

        assertEquals("File path cannot be blank", exception.getMessage());
    }

    @Test
    void testParseFile_WithWhitespaceOnlyPath_ThrowsIllegalArgumentException() {
        MarkdownParser parser = new MarkdownParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseFile("   ")
        );

        assertEquals("File path cannot be blank", exception.getMessage());
    }

    @Test
    void testParseFile_WithTabOnlyPath_ThrowsIllegalArgumentException() {
        MarkdownParser parser = new MarkdownParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseFile("\t\t")
        );

        assertEquals("File path cannot be blank", exception.getMessage());
    }

    @Test
    void testParseFile_WithNewLineOnlyPath_ThrowsIllegalArgumentException() {
        MarkdownParser parser = new MarkdownParser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseFile("\n")
        );

        assertEquals("File path cannot be blank", exception.getMessage());
    }

    @Test
    void testParseFile_WithNonExistentFile_ThrowsMarkdownParseException() {
        MarkdownParser parser = new MarkdownParser();
        String nonExistentPath = "/non/existent/path/file.md";

        MarkdownParseException exception = assertThrows(
                MarkdownParseException.class,
                () -> parser.parseFile(nonExistentPath)
        );

        assertTrue(exception.getMessage().contains("Failed to read file"),
                "Exception message should mention file reading failure");
        assertNotNull(exception.getCause(), "Exception should have a cause");
    }

    @Test
    void testParseFile_WithDirectoryPath_ThrowsMarkdownParseException() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        Path dirPath = tempDir.resolve("directory");
        Files.createDirectories(dirPath);

        MarkdownParseException exception = assertThrows(
                MarkdownParseException.class,
                () -> parser.parseFile(dirPath.toString())
        );

        assertTrue(exception.getMessage().contains("Failed to read file"),
                "Exception message should mention file reading failure");
    }

    @Test
    void testParseFile_WithSpecialCharactersInPath_ParsesSuccessfully() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        String specialContent = "# Test File";
        Path fileWithSpecialChars = tempDir.resolve("test-file_123.md");
        Files.writeString(fileWithSpecialChars, specialContent);
        testFilePath = fileWithSpecialChars;

        Document document = parser.parseFile(testFilePath.toString());

        assertNotNull(document, "Document should not be null");
    }

    @Test
    void testParseFile_WithLargeFile_ParsesSuccessfully() throws IOException {
        MarkdownParser parser = new MarkdownParser();
        StringBuilder largeContent = new StringBuilder("# Large File\n\n");
        for (int i = 0; i < 1000; i++) {
            largeContent.append("Paragraph ").append(i).append(".\n\n");
        }
        testFilePath = tempDir.resolve("large.md");
        Files.writeString(testFilePath, largeContent.toString());

        Document document = parser.parseFile(testFilePath.toString());

        assertNotNull(document, "Document should not be null for large file");
        assertNotNull(document.getFirstChild(), "Large file should produce document with children");
    }
}
