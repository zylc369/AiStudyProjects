package com.md2word;

import com.md2word.generator.WordGenerator;
import com.md2word.generator.PDFGenerator;
import com.md2word.parser.MarkdownParser;
import com.vladsch.flexmark.util.ast.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main entry point for the Markdown to Document Converter CLI tool.
 *
 * <p>This command-line application converts Markdown files (.md) to Word documents (.docx)
 * or PDF documents (.pdf) with rich text formatting preserved.</p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main &lt;input.md&gt; &lt;output.docx|.pdf&gt;
 * </pre>
 *
 * <p><b>Arguments:</b></p>
 * <ul>
 *   <li>input.md - Path to the input Markdown file to convert</li>
 *   <li>output.docx or output.pdf - Path where the output document will be created</li>
 * </ul>
 *
 * <p><b>Examples:</b></p>
 * <pre>
 * java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main README.md output.docx
 * java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main README.md output.pdf
 * </pre>
 *
 * <p>The converter supports:</p>
 * <ul>
 *   <li>Headings (levels 1-6)</li>
 *   <li>Text formatting (bold, italic, bold-italic)</li>
 *   <li>Clickable hyperlinks</li>
 *   <li>Bulleted and numbered lists (including nested lists)</li>
 *   <li>Code blocks and inline code</li>
 *   <li>Blockquotes</li>
 *   <li>Images (embedded from local file paths)</li>
 * </ul>
 */
public class Main {

    /**
     * Main entry point for the Markdown to Document Converter.
     *
     * @param args Command-line arguments: input.md path and output.docx/.pdf path
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 2) {
            printUsage();
            System.exit(1);
        }

        Path inputPath = Paths.get(args[0]);
        Path outputPath = Paths.get(args[1]);
        String outputFileName = args[1];

        // Determine output format based on file extension
        boolean isPdfOutput = outputFileName.toLowerCase().endsWith(".pdf");
        boolean isDocxOutput = outputFileName.toLowerCase().endsWith(".docx");

        // Validate output file extension
        if (!isPdfOutput && !isDocxOutput) {
            System.err.println("Error: Output file must have .docx or .pdf extension");
            System.err.println("  Got: " + outputFileName);
            System.exit(1);
        }

        try {
            // Validate input file exists and is readable
            if (!Files.exists(inputPath)) {
                System.err.println("Error: Input file does not exist: " + inputPath);
                System.exit(1);
            }

            if (!Files.isReadable(inputPath)) {
                System.err.println("Error: Input file is not readable: " + inputPath);
                System.exit(1);
            }

            // Create parent directories for output file if needed
            Path outputParent = outputPath.getParent();
            if (outputParent != null && !Files.exists(outputParent)) {
                Files.createDirectories(outputParent);
            }

            // Read Markdown content from input file
            String markdownContent = Files.readString(inputPath);

            // Parse Markdown into AST
            MarkdownParser parser = new MarkdownParser();
            Document ast = parser.parse(markdownContent);

            // Generate output document based on format
            if (isPdfOutput) {
                // Generate PDF document
                PDFGenerator generator = new PDFGenerator();
                generator.generate(ast, outputPath);
            } else {
                // Generate Word document
                WordGenerator generator = new WordGenerator();
                generator.generate(ast, outputPath);
            }

            // Report success
            System.out.println("Conversion successful: " + inputPath + " -> " + outputPath);

        } catch (IOException e) {
            System.err.println("Error: Failed to convert file - " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid argument - " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Prints usage information to standard error.
     */
    private static void printUsage() {
        System.err.println("Usage: java -cp <jar-file> com.md2word.Main <input.md> <output.docx|output.pdf>");
        System.err.println();
        System.err.println("Arguments:");
        System.err.println("  input.md          Path to the input Markdown file");
        System.err.println("  output.docx/.pdf  Path to the output Word document (.docx) or PDF document (.pdf)");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("  java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main README.md output.docx");
        System.err.println("  java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main README.md output.pdf");
    }
}
