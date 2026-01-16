package com.markdowntoword.converter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Converter for adding Markdown-style headings to Word documents.
 * Converts heading levels 1-6 to Word paragraph styles Heading1 through Heading6.
 */
public class HeadingConverter {

    private static final int MIN_HEADING_LEVEL = 1;
    private static final int MAX_HEADING_LEVEL = 6;

    private final XWPFDocument document;

    /**
     * Creates a HeadingConverter that will add headings to the specified document.
     *
     * @param document the XWPFDocument to add headings to
     * @throws IllegalArgumentException if document is null
     */
    public HeadingConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a heading with the specified text and level to the document.
     * The heading style in Word will be "Heading1" through "Heading6" based on the level.
     *
     * @param text  the heading text content
     * @param level the heading level (1-6, where 1 is the highest level)
     * @throws IllegalArgumentException if text is null
     * @throws IllegalArgumentException if level is less than 1
     * @throws IllegalArgumentException if level is greater than 6
     */
    public void addHeading(String text, int level) {
        if (text == null) {
            throw new IllegalArgumentException("Heading text cannot be null");
        }
        if (level < MIN_HEADING_LEVEL) {
            throw new IllegalArgumentException(
                "Heading level cannot be less than " + MIN_HEADING_LEVEL + ", was: " + level
            );
        }
        if (level > MAX_HEADING_LEVEL) {
            throw new IllegalArgumentException(
                "Heading level cannot be greater than " + MAX_HEADING_LEVEL + ", was: " + level
            );
        }

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setStyle("Heading" + level);
        paragraph.createRun().setText(text);
    }

    /**
     * Returns the underlying XWPFDocument instance.
     * This allows access to advanced Apache POI features if needed.
     *
     * @return the XWPFDocument instance
     */
    protected XWPFDocument getDocument() {
        return document;
    }
}
