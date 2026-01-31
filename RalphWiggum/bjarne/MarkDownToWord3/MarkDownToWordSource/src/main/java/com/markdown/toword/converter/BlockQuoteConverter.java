package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.util.ast.Node;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Converter for blockquote elements.
 */
public class BlockQuoteConverter {

    private final InlineTextConverter inlineTextConverter;

    public BlockQuoteConverter() {
        this.inlineTextConverter = new InlineTextConverter();
    }

    /**
     * Converts a BlockQuote node to Word paragraphs with indentation and italic formatting.
     *
     * @param blockQuote The BlockQuote node from the AST
     * @param document The Word document to add content to
     */
    public void convertBlockQuote(BlockQuote blockQuote, XWPFDocument document) {
        // Create a paragraph for the blockquote
        XWPFParagraph paragraph = document.createParagraph();

        // Set indentation (720 twips = 0.5 inch)
        paragraph.setIndentationLeft(720);

        // Set italic formatting for the blockquote content
        // Process child nodes
        Node child = blockQuote.getFirstChild();
        while (child != null) {
            if (child instanceof Paragraph para) {
                // Process paragraph content with italic
                processItalicParagraph(paragraph, para.getFirstChild());
            } else if (child instanceof BlockQuote nestedBlockQuote) {
                // Handle nested blockquotes recursively
                convertBlockQuote(nestedBlockQuote, document);
            } else {
                // Process other content with italic
                processItalicParagraph(paragraph, child);
            }
            child = child.getNext();
        }

        // If no content, add empty text
        if (paragraph.getRuns().isEmpty()) {
            XWPFRun run = paragraph.createRun();
            run.setText("");
            run.setItalic(true);
        }

        // Add spacing after blockquote
        paragraph.setSpacingAfter(100);
    }

    /**
     * Processes inline content with italic formatting.
     *
     * @param paragraph The Word paragraph to add runs to
     * @param node The starting node to process
     */
    private void processItalicParagraph(XWPFParagraph paragraph, Node node) {
        while (node != null) {
            if (node instanceof com.vladsch.flexmark.ast.Text textNode) {
                XWPFRun run = paragraph.createRun();
                run.setItalic(true);
                run.setText(textNode.getChars().toString());
            } else {
                // For other node types, use inlineTextConverter
                // but wrap the result with italic
                // This is a simplified approach - ideally we'd modify inlineTextConverter
                // to accept formatting options
                inlineTextConverter.processInlineNodes(paragraph, node);
            }
            node = node.getNext();
        }
    }
}
