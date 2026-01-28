package com.markdown.toword;

import com.markdown.toword.converter.HeaderConverter;
import com.markdown.toword.model.ConversionException;
import com.markdown.toword.parser.MarkdownParser;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main converter class that transforms Markdown content into Word documents.
 */
public class MarkdownConverter {

    private final MarkdownParser parser;
    private final HeaderConverter headerConverter;

    /**
     * Creates a new MarkdownConverter with a default parser.
     */
    public MarkdownConverter() {
        this.parser = new MarkdownParser();
        this.headerConverter = new HeaderConverter();
    }

    /**
     * Converts a Markdown string to a Word document.
     *
     * @param markdownContent The Markdown content as a string
     * @param outputPath The path where the .docx file will be created
     * @throws ConversionException if conversion fails
     */
    public void convert(String markdownContent, String outputPath) throws ConversionException {
        if (markdownContent == null) {
            throw new ConversionException("Markdown content cannot be null");
        }
        if (outputPath == null || outputPath.trim().isEmpty()) {
            throw new ConversionException("Output path cannot be null or empty");
        }

        try (XWPFDocument document = new XWPFDocument()) {
            // Parse markdown into AST
            var astDocument = parser.parse(markdownContent);

            // Convert headers
            headerConverter.convertHeaders(document, astDocument);

            // Ensure output directory exists
            Path outputFile = Paths.get(outputPath);
            Path parentDir = outputFile.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // Write the document to file
            try (FileOutputStream out = new FileOutputStream(outputFile.toFile())) {
                document.write(out);
            }
        } catch (IOException e) {
            throw new ConversionException("Failed to write Word document to: " + outputPath, e);
        }
    }

    /**
     * Converts a Markdown file to a Word document.
     *
     * @param markdownFilePath Path to the input .md file
     * @param outputPath The path where the .docx file will be created
     * @throws IOException if file operations fail
     * @throws ConversionException if conversion fails
     */
    public void convertFile(String markdownFilePath, String outputPath) throws IOException, ConversionException {
        if (markdownFilePath == null || markdownFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Markdown file path cannot be null or empty");
        }

        File inputFile = new File(markdownFilePath);
        if (!inputFile.exists()) {
            throw new IOException("Markdown file not found: " + markdownFilePath);
        }
        if (!inputFile.canRead()) {
            throw new IOException("Cannot read Markdown file: " + markdownFilePath);
        }

        // Read the markdown content from file
        String markdownContent;
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            markdownContent = new String(fis.readAllBytes());
        }

        // Convert the content
        convert(markdownContent, outputPath);
    }
}
