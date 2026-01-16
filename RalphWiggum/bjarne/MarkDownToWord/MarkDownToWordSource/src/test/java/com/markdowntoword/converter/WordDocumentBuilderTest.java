package com.markdowntoword.converter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WordDocumentBuilder class.
 */
class WordDocumentBuilderTest {

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

    @Test
    void testConstructor_InitializesDocument() {
        WordDocumentBuilder builder = new WordDocumentBuilder();
        assertNotNull(builder.getDocument(), "Document should be initialized");
    }

    @Test
    void testAddParagraph_WithValidText_AddsParagraph() {
        WordDocumentBuilder builder = new WordDocumentBuilder();
        String testText = "This is a test paragraph";

        assertDoesNotThrow(() -> builder.addParagraph(testText));

        XWPFDocument document = builder.getDocument();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size(), "Should have one paragraph");
        assertEquals(testText, paragraphs.get(0).getText(), "Paragraph text should match");
    }

    @Test
    void testAddParagraph_WithNullText_ThrowsIllegalArgumentException() {
        WordDocumentBuilder builder = new WordDocumentBuilder();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.addParagraph(null)
        );

        assertEquals("Paragraph text cannot be null", exception.getMessage());
    }

    @Test
    void testAddParagraph_WithEmptyString_AddsParagraph() {
        WordDocumentBuilder builder = new WordDocumentBuilder();
        String emptyText = "";

        assertDoesNotThrow(() -> builder.addParagraph(emptyText));

        XWPFDocument document = builder.getDocument();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size(), "Should have one paragraph");
        assertEquals(emptyText, paragraphs.get(0).getText(), "Paragraph text should be empty");
    }

    @Test
    void testAddParagraph_MultipleParagraphs_AddsAllParagraphs() {
        WordDocumentBuilder builder = new WordDocumentBuilder();

        builder.addParagraph("First paragraph");
        builder.addParagraph("Second paragraph");
        builder.addParagraph("Third paragraph");

        XWPFDocument document = builder.getDocument();
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size(), "Should have three paragraphs");
        assertEquals("First paragraph", paragraphs.get(0).getText());
        assertEquals("Second paragraph", paragraphs.get(1).getText());
        assertEquals("Third paragraph", paragraphs.get(2).getText());
    }

    @Test
    void testSave_WithValidPath_SavesDocument() throws IOException {
        WordDocumentBuilder builder = new WordDocumentBuilder();
        builder.addParagraph("Test content");
        testFilePath = tempDir.resolve("test-document.docx");

        assertDoesNotThrow(() -> builder.save(testFilePath.toString()));

        assertTrue(Files.exists(testFilePath), "Output file should exist");
        assertTrue(Files.size(testFilePath) > 0, "Output file should not be empty");
    }

    @Test
    void testSave_WithNestedPath_CreatesDirectories() throws IOException {
        WordDocumentBuilder builder = new WordDocumentBuilder();
        builder.addParagraph("Test content");
        Path nestedDir = tempDir.resolve("nested").resolve("directory");
        testFilePath = nestedDir.resolve("test-document.docx");

        assertDoesNotThrow(() -> builder.save(testFilePath.toString()));

        assertTrue(Files.exists(testFilePath), "Output file should exist");
        assertTrue(Files.exists(nestedDir), "Nested directories should be created");
    }

    @Test
    void testSave_WithNullPath_ThrowsIllegalArgumentException() {
        WordDocumentBuilder builder = new WordDocumentBuilder();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.save(null)
        );

        assertEquals("File path cannot be null", exception.getMessage());
    }

    @Test
    void testSave_WithBlankPath_ThrowsIllegalArgumentException() {
        WordDocumentBuilder builder = new WordDocumentBuilder();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.save("")
        );

        assertEquals("File path cannot be blank", exception.getMessage());
    }

    @Test
    void testSave_WithWhitespacePath_ThrowsIllegalArgumentException() {
        WordDocumentBuilder builder = new WordDocumentBuilder();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.save("   ")
        );

        assertEquals("File path cannot be blank", exception.getMessage());
    }

    @Test
    void testSavedDocument_CanBeReopenedAndContentMatches() throws IOException {
        WordDocumentBuilder builder = new WordDocumentBuilder();
        String originalContent = "Original paragraph content";
        builder.addParagraph(originalContent);
        testFilePath = tempDir.resolve("reopen-test.docx");

        builder.save(testFilePath.toString());

        try (FileInputStream inputStream = new FileInputStream(testFilePath.toFile());
             XWPFDocument reopenedDocument = new XWPFDocument(inputStream)) {

            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(1, paragraphs.size(), "Should have one paragraph");
            assertEquals(originalContent, paragraphs.get(0).getText(), "Content should match");
        }
    }

    @Test
    void testMultipleParagraphs_SavedAndReopened_AllContentPreserved() throws IOException {
        WordDocumentBuilder builder = new WordDocumentBuilder();
        builder.addParagraph("First line");
        builder.addParagraph("Second line");
        builder.addParagraph("Third line");
        testFilePath = tempDir.resolve("multiparagraph-test.docx");

        builder.save(testFilePath.toString());

        try (FileInputStream inputStream = new FileInputStream(testFilePath.toFile());
             XWPFDocument reopenedDocument = new XWPFDocument(inputStream)) {

            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(3, paragraphs.size(), "Should have three paragraphs");
            assertEquals("First line", paragraphs.get(0).getText());
            assertEquals("Second line", paragraphs.get(1).getText());
            assertEquals("Third line", paragraphs.get(2).getText());
        }
    }
}
