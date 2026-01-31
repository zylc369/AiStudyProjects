package com.markdown.toword.parser;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Parser for Markdown content using flexmark library.
 * Parses Markdown strings into an Abstract Syntax Tree (AST) for processing.
 */
public class MarkdownParser {

    private final Parser parser;

    /**
     * Creates a new MarkdownParser with default options.
     */
    public MarkdownParser() {
        MutableDataSet options = new MutableDataSet();
        this.parser = Parser.builder(options).build();
    }

    /**
     * Parses Markdown content into an AST Document.
     *
     * @param markdownContent The Markdown content as a string
     * @return The root Document node of the AST
     * @throws IllegalArgumentException if markdownContent is null
     */
    public Document parse(String markdownContent) {
        if (markdownContent == null) {
            throw new IllegalArgumentException("Markdown content cannot be null");
        }
        return parser.parse(markdownContent);
    }
}
