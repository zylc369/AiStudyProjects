package com.md2word;

import com.md2word.generator.WordGenerator;
import com.md2word.parser.MarkdownParser;
import com.vladsch.flexmark.util.ast.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main entry point for the Markdown to Word Converter CLI tool.
 *
 * <p>This command-line application converts Markdown files (.md) to Word documents (.docx)
 * with rich text formatting preserved.</p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main &lt;input.md&gt; &lt;output.docx&gt;
 * </pre>
 *
 * <p><b>Arguments:</b></p>
 * <ul>
 *   <li>input.md - Path to the input Markdown file to convert</li>
 *   <li>output.docx - Path where the Word document will be created</li>
 * </ul>
 *
 * <p><b>Example:</b></p>
 * <pre>
 * java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main README.md output.docx
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
     * Main entry point for the Markdown to Word Converter.
     *
     * @param args Command-line arguments: input.md path and output.docx path
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 2) {
            printUsage();
            System.exit(1);
        }

        Path inputPath = Paths.get(args[0]);
        Path outputPath = Paths.get(args[1]);

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

            // Generate Word document from AST
            WordGenerator generator = new WordGenerator();
            generator.generate(ast, outputPath);

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
        System.err.println("Usage: java -cp <jar-file> com.md2word.Main <input.md> <output.docx>");
        System.err.println();
        System.err.println("Arguments:");
        System.err.println("  input.md    Path to the input Markdown file");
        System.err.println("  output.docx Path to the output Word document");
        System.err.println();
        System.err.println("Example:");
        System.err.println("  java -cp target/md2word-1.0-SNAPSHOT.jar com.md2word.Main README.md output.docx");
    }
}
