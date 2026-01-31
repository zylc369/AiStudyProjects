package com.md2word.generator;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * WordGenerator uses Apache POI to generate Word documents (.docx) from a Markdown AST.
 *
 * <p>This class traverses a flexmark Document AST and creates a Word document with
 * equivalent formatting and structure using Apache POI's XWPF (XML Word Processor Format).</p>
 *
 * <p>Currently supported elements:</p>
 * <ul>
 *   <li>Headings (levels 1-6) with proper Word styles</li>
 * </ul>
 *
 * <p>Future implementations will add:</p>
 * <ul>
 *   <li>Text formatting (bold, italic)</li>
 *   <li>Paragraphs, lists, tables, and other elements</li>
 * </ul>
 */
public class WordGenerator {

    /**
     * Generates a Word document from a Markdown AST.
     *
     * @param ast The flexmark Document AST to traverse and convert
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

        // Traverse the AST and convert each node
        if (ast != null) {
            for (Node node : ast.getChildren()) {
                if (node instanceof Heading) {
                    processHeading((Heading) node, document);
                }
                // Other node types (Paragraph, List, etc.) will be added in future tasks
            }
        }

        // Write the document to the output file
        try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
            document.write(out);
        } finally {
            document.close();
        }
    }

    /**
     * Processes a Markdown heading node and adds it to the Word document.
     *
     * @param heading The flexmark Heading node to process
     * @param document The Word document to add the heading to
     */
    private void processHeading(Heading heading, XWPFDocument document) {
        int level = heading.getLevel();

        // Validate heading level (flexmark should guarantee 1-6)
        if (level < 1 || level > 6) {
            level = 1; // Default to Heading1 if invalid
        }

        // Extract text content from heading by traversing child nodes
        StringBuilder textBuilder = new StringBuilder();
        for (Node child : heading.getChildren()) {
            if (child instanceof Text) {
                textBuilder.append(((Text) child).getChars());
            }
            // For now, ignore inline formatting (Emphasis, Strong, Link, etc.)
            // These will be handled in the "Add text formatting support" task
        }

        String text = textBuilder.toString();

        // Create paragraph with appropriate heading style
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setStyle("Heading" + level);

        // Add text content
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }
}
