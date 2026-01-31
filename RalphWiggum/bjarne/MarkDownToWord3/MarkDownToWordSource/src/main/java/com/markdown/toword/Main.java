package com.markdown.toword;

import com.markdown.toword.model.ConversionException;

import java.io.IOException;

/**
 * Main entry point for the Markdown to Word converter CLI.
 */
public class Main {

    /**
     * Main method for CLI execution.
     *
     * @param args Command-line arguments: [input.md] [output.docx]
     */
    public static void main(String[] args) {
        // Validate arguments
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        // Validate input file extension
        if (!inputPath.toLowerCase().endsWith(".md")) {
            System.err.println("Error: Input file must have .md extension");
            System.exit(1);
        }

        // Validate output file extension
        if (!outputPath.toLowerCase().endsWith(".docx")) {
            System.err.println("Error: Output file must have .docx extension");
            System.exit(1);
        }

        // Create converter and convert
        MarkdownConverter converter = new MarkdownConverter();
        try {
            System.out.println("Converting: " + inputPath + " -> " + outputPath);
            converter.convertFile(inputPath, outputPath);
            System.out.println("Conversion completed successfully!");
        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (ConversionException e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.err.println("Markdown to Word Converter");
        System.err.println("Usage: java -jar markdown-to-word-1.0.0.jar <input.md> <output.docx>");
        System.err.println();
        System.err.println("Arguments:");
        System.err.println("  input.md   Path to the input Markdown file (.md)");
        System.err.println("  output.docx Path to the output Word document (.docx)");
        System.err.println();
        System.err.println("Example:");
        System.err.println("  java -jar markdown-to-word-1.0.0.jar README.md README.docx");
    }
}
