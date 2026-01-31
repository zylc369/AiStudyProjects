package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
            } else if (node instanceof Strikethrough strike) {
                // ~~strikethrough~~
                processStrikethroughText(paragraph, strike);
            } else if (node instanceof Code code) {
                // `inline code`
                processInlineCodeText(paragraph, code);
            } else if (node instanceof Link link) {
                // [text](url)
                processHyperlinkText(paragraph, link);
            } else if (node instanceof Image image) {
                // ![alt](url)
                processImageText(paragraph, image);
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
     * Recursively processes children to handle nested formatting (e.g., bold+italic).
     */
    private void processBoldText(XWPFParagraph paragraph, StrongEmphasis strong) {
        // Create a run for each child to properly handle nested formatting
        Node child = strong.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                // Plain text within bold - create a bold run
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setText(textNode.getChars().toString());
            } else if (child instanceof Emphasis em) {
                // Nested italic within bold (***bolditalic***)
                processBoldAndItalicText(paragraph, em);
            } else if (child instanceof StrongEmphasis nestedStrong) {
                // Double-nested bold - just treat as bold
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setText(getNodeText(nestedStrong));
            } else {
                // Other node types - treat as bold text
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setText(getNodeText(child));
            }
            child = child.getNext();
        }
    }

    /**
     * Processes italic text (Emphasis nodes).
     * Recursively processes children to handle nested formatting (e.g., italic+bold).
     */
    private void processItalicText(XWPFParagraph paragraph, Emphasis em) {
        // Create a run for each child to properly handle nested formatting
        Node child = em.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                // Plain text within italic - create an italic run
                XWPFRun run = paragraph.createRun();
                run.setItalic(true);
                run.setText(textNode.getChars().toString());
            } else if (child instanceof StrongEmphasis strong) {
                // Nested bold within italic (***bolditalic***)
                processItalicAndBoldText(paragraph, strong);
            } else if (child instanceof Emphasis nestedEm) {
                // Double-nested italic - just treat as italic
                XWPFRun run = paragraph.createRun();
                run.setItalic(true);
                run.setText(getNodeText(nestedEm));
            } else {
                // Other node types - treat as italic text
                XWPFRun run = paragraph.createRun();
                run.setItalic(true);
                run.setText(getNodeText(child));
            }
            child = child.getNext();
        }
    }

    /**
     * Processes bold+italic text (StrongEmphasis containing Emphasis).
     * Handles the ***bolditalic*** case where bold is outer, italic is inner.
     */
    private void processBoldAndItalicText(XWPFParagraph paragraph, Emphasis em) {
        // Process children of the nested Emphasis
        Node child = em.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setItalic(true);
                run.setText(textNode.getChars().toString());
            } else {
                // Handle any other nested content with both formats
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setItalic(true);
                run.setText(getNodeText(child));
            }
            child = child.getNext();
        }
    }

    /**
     * Processes italic+bold text (Emphasis containing StrongEmphasis).
     * Handles the ***bolditalic*** case where italic is outer, bold is inner.
     */
    private void processItalicAndBoldText(XWPFParagraph paragraph, StrongEmphasis strong) {
        // Process children of the nested StrongEmphasis
        Node child = strong.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setItalic(true);
                run.setText(textNode.getChars().toString());
            } else {
                // Handle any other nested content with both formats
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setItalic(true);
                run.setText(getNodeText(child));
            }
            child = child.getNext();
        }
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

    /**
     * Processes strikethrough text (Strikethrough nodes).
     * Handles the ~~strikethrough~~ Markdown syntax.
     */
    private void processStrikethroughText(XWPFParagraph paragraph, Strikethrough strike) {
        // Process children of the Strikethrough node
        Node child = strike.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                XWPFRun run = paragraph.createRun();
                run.setStrikeThrough(true);
                run.setText(textNode.getChars().toString());
            } else {
                // Handle any other nested content with strikethrough
                XWPFRun run = paragraph.createRun();
                run.setStrikeThrough(true);
                run.setText(getNodeText(child));
            }
            child = child.getNext();
        }
    }

    /**
     * Processes inline code (Code nodes).
     * Handles the `inline code` Markdown syntax with monospace font.
     */
    private void processInlineCodeText(XWPFParagraph paragraph, Code code) {
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Courier New");
        run.setText(code.getText().toString());
    }

    /**
     * Processes hyperlinks (Link nodes).
     * Handles the [text](url) Markdown syntax.
     */
    private void processHyperlinkText(XWPFParagraph paragraph, Link link) {
        String url = link.getUrl().toString();
        String text = link.getText().toString();

        if (text.isEmpty()) {
            // If no text provided, use the URL as the display text
            text = url;
        }

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setColor("0000FF"); // Blue color for hyperlink
        run.setUnderline(UnderlinePatterns.SINGLE);

        // Note: For actual clickable hyperlinks in Word, we would need to use
        // XWPFHyperlink or add hyperlink relationships to the document.
        // This is a simplified implementation that shows visual hyperlink style.
        // TODO: Implement actual clickable hyperlink using document relationships
    }

    /**
     * Processes images (Image nodes).
     * Handles the ![alt](url) Markdown syntax.
     */
    private void processImageText(XWPFParagraph paragraph, Image image) {
        String url = image.getUrl().toString();
        String altText = image.getText().toString();

        // For now, display the alt text as a placeholder
        // Full image embedding would require:
        // 1. Downloading the image from URL
        // 2. Detecting image type (PNG, JPEG, etc.)
        // 3. Adding image data to document using XWPFPictureData
        // 4. Using XWPFRun.addPicture() to embed it

        XWPFRun run = paragraph.createRun();
        if (!altText.isEmpty()) {
            run.setText("[Image: " + altText + "]");
        } else {
            run.setText("[Image: " + url + "]");
        }
        run.setItalic(true);
        run.setColor("808080"); // Gray color for placeholder

        // TODO: Implement actual image embedding:
        // - Download image from URL
        // - Determine picture type from URL/Content-Type
        // - Use paragraph.getDocument().addPictureData()
        // - Use run.addPicture() with appropriate parameters
    }
}
