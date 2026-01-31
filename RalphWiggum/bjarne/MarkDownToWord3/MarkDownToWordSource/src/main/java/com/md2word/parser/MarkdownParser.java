package com.md2word.parser;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.Arrays;

/**
 * MarkdownParser uses flexmark-java to parse Markdown content into an Abstract Syntax Tree (AST).
 *
 * <p>This parser supports CommonMark and GitHub Flavored Markdown (GFM), including:</p>
 * <ul>
 *   <li>Headings (levels 1-6)</li>
 *   <li>Text formatting (bold, italic, strikethrough)</li>
 *   <li>Links and images</li>
 *   <li>Ordered and unordered lists</li>
   <li>Tables (GFM extension)</li>
 *   <li>Code blocks (fenced and indented)</li>
 *   <li>Blockquotes</li>
 *   <li>Horizontal rules</li>
 * </ul>
 *
 * <p>The parser produces a flexmark Document AST that can be traversed to generate
 * Word documents or perform other transformations.</p>
 */
public class MarkdownParser {

    private final Parser parser;

    /**
     * Constructs a MarkdownParser with default flexmark options.
     * The parser is configured to support CommonMark and GitHub Flavored Markdown.
     * TablesExtension is enabled to support GFM tables.
     */
    public MarkdownParser() {
        // Configure parser with TablesExtension to support GFM tables
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        this.parser = Parser.builder(options).build();
    }

    /**
     * Parses Markdown content into an Abstract Syntax Tree (AST).
     *
     * @param markdownContent The Markdown content as a string
     * @return A flexmark Document representing the AST
     * @throws IllegalArgumentException if markdownContent is null
     */
    public Document parse(String markdownContent) {
        if (markdownContent == null) {
            throw new IllegalArgumentException("Markdown content cannot be null");
        }

        return parser.parse(markdownContent);
    }

    /**
     * Parses Markdown content using a static utility method.
     * This is a convenience method for one-off parsing without creating an instance.
     *
     * @param markdownContent The Markdown content as a string
     * @return A flexmark Document representing the AST
     * @throws IllegalArgumentException if markdownContent is null
     */
    public static Document parseStatic(String markdownContent) {
        MarkdownParser parser = new MarkdownParser();
        return parser.parse(markdownContent);
    }
}
