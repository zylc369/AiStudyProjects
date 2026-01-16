package com.markdowntoword.parser;

import com.markdowntoword.exception.MarkdownParseException;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Parser for converting Markdown content into an AST using Flexmark-java.
 */
public class MarkdownParser {

    private final Parser parser;

    /**
     * Creates a MarkdownParser with default Flexmark options.
     * The parser is configured with common extensions and features.
     */
    public MarkdownParser() {
        MutableDataSet options = new MutableDataSet();
        this.parser = Parser.builder(options).build();
    }

    /**
     * Parses a Markdown string and returns the AST Document.
     *
     * @param markdownContent the Markdown content to parse
     * @return the Document AST representing the parsed Markdown
     * @throws IllegalArgumentException if markdownContent is null
     */
    public Document parse(String markdownContent) {
        if (markdownContent == null) {
            throw new IllegalArgumentException("Markdown content cannot be null");
        }
        return parser.parse(markdownContent);
    }

    /**
     * Reads a Markdown file and returns the AST Document.
     *
     * @param filePath the path to the Markdown file to parse
     * @return the Document AST representing the parsed Markdown file
     * @throws IllegalArgumentException if filePath is null
     * @throws IllegalArgumentException if filePath is blank
     * @throws MarkdownParseException if the file cannot be read
     */
    public Document parseFile(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        if (filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be blank");
        }

        Path path = Paths.get(filePath);
        try {
            String markdownContent = Files.readString(path);
            return parse(markdownContent);
        } catch (IOException e) {
            throw new MarkdownParseException("Failed to read file: " + filePath, e);
        }
    }

    /**
     * Returns the underlying Flexmark Parser instance.
     * This allows access to advanced configuration if needed.
     *
     * @return the Flexmark Parser instance
     */
    protected Parser getParser() {
        return parser;
    }
}
