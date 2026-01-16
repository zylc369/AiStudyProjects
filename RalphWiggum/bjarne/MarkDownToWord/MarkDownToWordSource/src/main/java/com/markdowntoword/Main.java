package com.markdowntoword;

import com.markdowntoword.converter.*;
import com.markdowntoword.exception.MarkdownParseException;
import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.util.ast.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main entry point for the Markdown to Word conversion tool.
 * Accepts command line arguments to convert Markdown files to Word (.docx) format.
 */
public class Main {

    private static final String DEFAULT_OUTPUT_EXTENSION = ".docx";

    /**
     * Main method that processes command line arguments and performs the conversion.
     *
     * @param args command line arguments:
     *             args[0]: input.md file path (required)
     *             args[1]: output.docx file path (optional, defaults to input filename with .docx extension)
     */
    public static void main(String[] args) {
        try {
            String inputFile = validateInputArgument(args);
            String outputFile = getOutputFilePath(args, inputFile);
            validateInputFileExists(inputFile);

            performConversion(inputFile, outputFile);

            System.out.println("Conversion successful. Output file: " + outputFile);
            printJarLocation();
            System.exit(0);
        } catch (IllegalArgumentException | MarkdownParseException | ConversionException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Validates that the input file argument is provided and not blank.
     *
     * @param args command line arguments
     * @return the input file path
     * @throws IllegalArgumentException if no input file is provided
     */
    private static String validateInputArgument(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No input file specified. Usage: java -jar MarkDownToWord-1.0.jar <input.md> [output.docx]");
        }
        String inputFile = args[0];
        if (inputFile.isBlank()) {
            throw new IllegalArgumentException("Input file path cannot be blank");
        }
        return inputFile;
    }

    /**
     * Validates that the input file exists and is readable.
     *
     * @param inputFile the input file path
     * @throws IllegalArgumentException if the file does not exist or is not readable
     */
    private static void validateInputFileExists(String inputFile) {
        Path path = Paths.get(inputFile);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Input file does not exist: " + inputFile);
        }
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("Input file is not readable: " + inputFile);
        }
    }

    /**
     * Determines the output file path.
     * If the output argument is provided, it is used directly.
     * Otherwise, the output path is generated from the input filename with .docx extension.
     *
     * @param args      command line arguments
     * @param inputFile the input file path
     * @return the output file path
     * @throws IllegalArgumentException if the output argument is blank
     */
    private static String getOutputFilePath(String[] args, String inputFile) {
        if (args.length > 1) {
            String outputFile = args[1];
            if (outputFile.isBlank()) {
                throw new IllegalArgumentException("Output file path cannot be blank");
            }
            return outputFile;
        }

        // Generate default output filename from input
        String defaultOutput = generateDefaultOutputPath(inputFile);
        System.out.println("No output file specified. Using default: " + defaultOutput);
        return defaultOutput;
    }

    /**
     * Generates a default output file path from the input file path.
     * The output filename is the same as the input filename with the extension replaced by .docx.
     *
     * @param inputFile the input file path
     * @return the default output file path with .docx extension
     */
    private static String generateDefaultOutputPath(String inputFile) {
        Path inputPath = Paths.get(inputFile);
        String fileName = inputPath.getFileName().toString();

        // Remove .md extension if present
        String outputFileName;
        if (fileName.toLowerCase().endsWith(".md")) {
            outputFileName = fileName.substring(0, fileName.length() - 3) + DEFAULT_OUTPUT_EXTENSION;
        } else {
            outputFileName = fileName + DEFAULT_OUTPUT_EXTENSION;
        }

        return inputPath.resolveSibling(outputFileName).toString();
    }

    /**
     * Prints the full absolute path to the JAR file containing this class.
     * This helps users identify the location of the tool.
     */
    private static void printJarLocation() {
        try {
            String jarLocation = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String absolutePath = Paths.get(jarLocation).toAbsolutePath().toString();
            System.out.println("Tool location: " + absolutePath);
        } catch (Exception e) {
            // If we can't determine the JAR location, skip this output
            // This can happen when running from an IDE or in certain test environments
        }
    }

    /**
     * Performs the Markdown to Word conversion.
     * This method orchestrates the parsing and conversion process using all available converters.
     *
     * @param inputFile  the input Markdown file path
     * @param outputFile the output Word file path
     * @throws MarkdownParseException if the Markdown file cannot be parsed
     * @throws ConversionException    if the Word document cannot be saved
     */
    private static void performConversion(String inputFile, String outputFile) {
        MarkdownParser parser = new MarkdownParser();
        Document document = parser.parseFile(inputFile);

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
}
