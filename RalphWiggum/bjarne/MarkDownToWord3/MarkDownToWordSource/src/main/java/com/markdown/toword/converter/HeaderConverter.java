package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.Document;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Converter for Markdown header elements to Word heading styles.
 */
public class HeaderConverter {

    private final InlineTextConverter inlineTextConverter;

    /**
     * Creates a new HeaderConverter.
     */
    public HeaderConverter() {
        this.inlineTextConverter = new InlineTextConverter();
    }

    /**
     * Converts headers from a Markdown AST to a Word document.
     *
     * @param document The Word document to add headers to
     * @param astDocument The Markdown AST document
     */
    public void convertHeaders(XWPFDocument document, Document astDocument) {
        // Iterate through all nodes in the document
        Node node = astDocument.getFirstChild();
        while (node != null) {
            if (node instanceof Heading heading) {
                convertHeading(document, heading);
            }
            node = node.getNext();
        }
    }

    /**
     * Converts a single heading to Word format.
     *
     * @param document The Word document
     * @param heading The heading node to convert
     */
    private void convertHeading(XWPFDocument document, Heading heading) {
        XWPFParagraph paragraph = document.createParagraph();
        String style = getHeadingStyle(heading.getLevel());
        paragraph.setStyle(style);

        // Process inline nodes (including bold, italic, etc.)
        inlineTextConverter.processInlineNodes(paragraph, heading.getFirstChild());
    }

    /**
     * Maps heading level to Word style name.
     *
     * @param level The heading level (1-6)
     * @return The Word style name (e.g., "Heading1")
     */
    private String getHeadingStyle(int level) {
        return switch (level) {
            case 1 -> "Heading1";
            case 2 -> "Heading2";
            case 3 -> "Heading3";
            case 4 -> "Heading4";
            case 5 -> "Heading5";
            case 6 -> "Heading6";
            default -> "Heading1";
        };
    }
}
