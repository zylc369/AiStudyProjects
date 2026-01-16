package com.markdowntoword.converter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builder for creating Word (.docx) documents using Apache POI.
 * Supports adding paragraphs and saving the document to disk.
 */
public class WordDocumentBuilder {

    private final XWPFDocument document;

    /**
     * Creates a new WordDocumentBuilder and initializes an empty XWPFDocument.
     */
    public WordDocumentBuilder() {
        this.document = new XWPFDocument();
    }

    /**
     * Adds a paragraph with the specified text content to the document.
     *
     * @param text the text content for the paragraph
     * @throws IllegalArgumentException if text is null
     */
    public void addParagraph(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Paragraph text cannot be null");
        }
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().setText(text);
    }

    /**
     * Saves the document to the specified file path.
     *
     * @param filePath the path where the document should be saved
     * @throws IllegalArgumentException if filePath is null
     * @throws IllegalArgumentException if filePath is blank
     * @throws ConversionException     if the document cannot be saved
     */
    public void save(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be blank");
        }

        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new ConversionException("Failed to create directory for file: " + filePath, e);
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            document.write(outputStream);
        } catch (IOException e) {
            throw new ConversionException("Failed to save document to file: " + filePath, e);
        }
    }

    /**
     * Returns the underlying XWPFDocument instance.
     * This allows access to advanced Apache POI features if needed.
     *
     * @return the XWPFDocument instance
     */
    protected XWPFDocument getDocument() {
        return document;
    }
}
