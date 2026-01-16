package com.markdowntoword.util;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility class for extracting and comparing text content from Markdown and Word documents.
 * This class provides methods to extract all text content from both formats for content verification.
 */
public class TextContentExtractor {

    /**
     * Extracts all text content from a Markdown file.
     * This reads the file as plain text and strips Markdown syntax characters
     * that won't appear in the converted Word document.
     *
     * @param markdownPath the path to the Markdown file
     * @return the normalized text content from the Markdown file
     * @throws IOException if the file cannot be read
     */
    public static String extractFromMarkdown(Path markdownPath) throws IOException {
        String content = Files.readString(markdownPath);
        // Strip Markdown syntax that won't appear in the converted Word document
        String cleanContent = content
                // Remove heading markers (# ## ### etc.) - use multiline flag
                .replaceAll("(?m)^#+\\s+", "")
                // Remove bold markers (**)
                .replaceAll("\\*\\*", "")
                // Remove italic markers (*)
                .replaceAll("(?<!\\*)\\*(?!\\*)", "")
                // Remove inline code markers (`)
                .replaceAll("`", "")
                // Remove code block markers (```)
                .replaceAll("```", "")
                // Remove link markers
                .replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1")
                // Remove table border characters
                .replaceAll("\\|", "")
                .replaceAll("(?m)^-+$", "") // Only remove table separator lines (---)
                // Remove list markers - use multiline flag
                .replaceAll("(?m)^\\s*[-*+]\\s+", "")
                .replaceAll("(?m)^\\s*\\d+\\.\\s+", "");
        return normalizeText(cleanContent);
    }

    /**
     * Extracts all text content from a Flexmark AST Document.
     * This recursively visits all nodes to collect text content.
     *
     * @param document the Flexmark Document AST
     * @return the normalized text content from the document
     */
    public static String extractFromMarkdownDocument(Node document) {
        StringBuilder textBuilder = new StringBuilder();
        appendTextFromNode(document, textBuilder);
        return normalizeText(textBuilder.toString());
    }

    /**
     * Extracts all text content from a Word document.
     * This extracts text from all paragraphs and all table cells.
     *
     * @param wordPath the path to the Word document
     * @return the normalized text content from the Word document
     * @throws IOException if the file cannot be read
     */
    public static String extractFromWord(Path wordPath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(wordPath.toFile());
             XWPFDocument document = new XWPFDocument(inputStream)) {

            StringBuilder textBuilder = new StringBuilder();

            // Extract text from paragraphs
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String paragraphText = paragraph.getText();
                if (!paragraphText.trim().isEmpty()) {
                    textBuilder.append(paragraphText).append(" ");
                }
            }

            // Extract text from tables
            List<XWPFTable> tables = document.getTables();
            for (XWPFTable table : tables) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph cellParagraph : cell.getParagraphs()) {
                            String cellText = cellParagraph.getText();
                            if (!cellText.trim().isEmpty()) {
                                textBuilder.append(cellText).append(" ");
                            }
                        }
                    }
                }
            }

            return normalizeText(textBuilder.toString());
        }
    }

    /**
     * Normalizes text by trimming whitespace and collapsing multiple spaces into single spaces.
     * This makes comparison more robust against minor formatting differences.
     *
     * @param text the text to normalize
     * @return the normalized text
     */
    public static String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        // Trim leading/trailing whitespace and collapse multiple spaces
        return text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Checks if all content from the source text is present in the target text.
     * This is a content presence check rather than exact match, since the Word converter
     * may structure content differently (e.g., multiple paragraphs).
     *
     * @param sourceText the original text content
     * @param targetText the converted text content to check
     * @return true if all significant content from source is present in target
     */
    public static boolean containsAllContent(String sourceText, String targetText) {
        String normalizedSource = normalizeText(sourceText);
        String normalizedTarget = normalizeText(targetText);

        // Split source into meaningful chunks (words/phrases) and check presence
        String[] sourceWords = normalizedSource.split("\\s+");
        String targetString = " " + normalizedTarget + " ";

        for (String word : sourceWords) {
            if (word.length() > 2) { // Skip very short words that might be noise
                if (!targetString.contains(" " + word + " ")) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Recursively appends text from a Flexmark AST node and its children.
     *
     * @param node the AST node
     * @param builder the StringBuilder to append text to
     */
    private static void appendTextFromNode(Node node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        // Append text content for Text nodes
        if (node instanceof Text textNode) {
            builder.append(textNode.getChars()).append(" ");
        }

        // Process children recursively
        Node child = node.getFirstChild();
        while (child != null) {
            appendTextFromNode(child, builder);
            child = child.getNext();
        }
    }
}
