package com.md2word.generator;

import com.vladsch.flexmark.util.ast.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * WordGenerator uses Apache POI to generate Word documents (.docx) from a Markdown AST.
 *
 * <p>This class traverses a flexmark Document AST and creates a Word document with
 * equivalent formatting and structure using Apache POI's XWPF (XML Word Processor Format).</p>
 *
 * <p>Currently this is a skeleton implementation that creates empty .docx files.
 * Future implementations will add:</p>
 * <ul>
 *   <li>Heading conversion</li>
 *   <li>Text formatting (bold, italic)</li>
 *   <li>Lists, tables, and other elements</li>
 * </ul>
 */
public class WordGenerator {

    /**
     * Generates a Word document from a Markdown AST.
     *
     * @param ast The flexmark Document AST (will be traversed in future implementations)
     * @param outputPath The path where the .docx file will be created
     * @throws IOException if the file cannot be written
     * @throws IllegalArgumentException if outputPath is null
     */
    public void generate(Document ast, Path outputPath) throws IOException {
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        // Create a new Word document
        XWPFDocument document = new XWPFDocument();

        // Add an empty paragraph (placeholder for future content)
        XWPFParagraph paragraph = document.createParagraph();

        // Write the document to the output file
        try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
            document.write(out);
        } finally {
            document.close();
        }
    }
}
