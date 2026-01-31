package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.util.ast.Node;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Converter for inline text formatting elements (bold, italic, etc.)
 */
public class InlineTextConverter {

    /**
     * Processes inline nodes within a paragraph and applies formatting.
     *
     * @param paragraph The Word paragraph to add runs to
     * @param node The starting node to process
     */
    public void processInlineNodes(XWPFParagraph paragraph, Node node) {
        while (node != null) {
            if (node instanceof Text textNode) {
                // Plain text - no special formatting
                XWPFRun run = paragraph.createRun();
                run.setText(textNode.getChars().toString());
            } else if (node instanceof StrongEmphasis strong) {
                // **bold** or __bold__
                processBoldText(paragraph, strong);
            } else if (node instanceof Emphasis em) {
                // *italic* or _italic_
                processItalicText(paragraph, em);
            } else {
                // For other node types, try to get text content
                // This handles cases where we haven't implemented specific converters yet
                String text = getNodeText(node);
                if (!text.isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                }
            }
            node = node.getNext();
        }
    }

    /**
     * Processes bold text (StrongEmphasis nodes).
     */
    private void processBoldText(XWPFParagraph paragraph, StrongEmphasis strong) {
        XWPFRun run = paragraph.createRun();
        run.setBold(true);

        // Process children of the StrongEmphasis node
        StringBuilder text = new StringBuilder();
        Node child = strong.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                text.append(textNode.getChars());
            }
            child = child.getNext();
        }
        run.setText(text.toString());
    }

    /**
     * Processes italic text (Emphasis nodes).
     */
    private void processItalicText(XWPFParagraph paragraph, Emphasis em) {
        XWPFRun run = paragraph.createRun();
        run.setItalic(true);

        // Process children of the Emphasis node
        StringBuilder text = new StringBuilder();
        Node child = em.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                text.append(textNode.getChars());
            }
            child = child.getNext();
        }
        run.setText(text.toString());
    }

    /**
     * Extracts text content from a node.
     * Used as fallback for unhandled node types.
     */
    private String getNodeText(Node node) {
        StringBuilder text = new StringBuilder();
        Node child = node.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                text.append(textNode.getChars());
            }
            child = child.getNext();
        }
        return text.toString();
    }
}
