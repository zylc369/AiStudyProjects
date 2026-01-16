package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;

/**
 * Converter for adding Markdown-style code blocks to Word documents.
 * Converts Flexmark FencedCodeBlock AST nodes (```language\ncode\n```) to Word paragraphs
 * with monospace font, background shading, and proper spacing for visual separation.
 */
public class CodeBlockConverter {

    private static final String MONOSPACE_FONT_FAMILY = "Courier New";
    private static final int SPACING_BEFORE_POINTS = 100;
    private static final int SPACING_AFTER_POINTS = 100;
    private static final String BACKGROUND_COLOR = "EEEEEE";

    private final XWPFDocument document;

    /**
     * Creates a CodeBlockConverter that will add code blocks to the specified document.
     *
     * @param document the XWPFDocument to add code blocks to
     * @throws IllegalArgumentException if document is null
     */
    public CodeBlockConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a code block with the specified code content to the document.
     * The code block will have monospace font, gray background shading,
     * and spacing before and after for visual separation.
     *
     * @param code the code content for the code block
     * @throws IllegalArgumentException if code is null
     */
    public void addCodeBlock(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code content cannot be null");
        }

        XWPFParagraph paragraph = document.createParagraph();

        // Set spacing for visual separation
        paragraph.setSpacingBefore(SPACING_BEFORE_POINTS);
        paragraph.setSpacingAfter(SPACING_AFTER_POINTS);

        // Set background shading
        CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr == null) {
            pPr = paragraph.getCTP().addNewPPr();
        }
        CTShd shd = pPr.isSetShd() ? pPr.getShd() : pPr.addNewShd();
        shd.setFill(BACKGROUND_COLOR);

        // Split code into lines for proper formatting
        String[] lines = code.split("\\r?\\n");
        if (lines.length == 0) {
            // Create a single run for empty code block
            XWPFRun run = paragraph.createRun();
            run.setFontFamily(MONOSPACE_FONT_FAMILY);
        } else if (lines.length == 1 && lines[0].isEmpty()) {
            // Single empty line
            XWPFRun run = paragraph.createRun();
            run.setFontFamily(MONOSPACE_FONT_FAMILY);
        } else {
            // Multiple lines - add line breaks between them
            boolean first = true;
            for (String line : lines) {
                XWPFRun run = paragraph.createRun();
                if (!first) {
                    run.addBreak();
                }
                run.setText(line);
                run.setFontFamily(MONOSPACE_FONT_FAMILY);
                first = false;
            }
        }
    }

    /**
     * Converts a Flexmark FencedCodeBlock AST node to a Word code block paragraph.
     * This method extracts the code content from the AST node using getContentChars()
     * and converts it to a Word paragraph with monospace font formatting and background shading.
     *
     * @param block the Flexmark FencedCodeBlock AST node to convert
     * @throws IllegalArgumentException if block is null
     */
    public void convertFencedCodeBlock(FencedCodeBlock block) {
        if (block == null) {
            throw new IllegalArgumentException("FencedCodeBlock cannot be null");
        }

        String code = getTextFromNode(block);
        addCodeBlock(code);
    }

    /**
     * Converts a Flexmark Document AST to Word code blocks.
     * This method iterates through the document's children and converts
     * all FencedCodeBlock nodes to Word paragraphs with monospace font
     * formatting, background shading, and spacing.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(FencedCodeBlock.class, this::convertFencedCodeBlock)
        );

        visitor.visit(document);
    }

    /**
     * Extracts the text content from a Flexmark AST node.
     * This method recursively visits child nodes to collect all text content.
     *
     * @param node the AST node to extract text from
     * @return the concatenated text content of the node and its children
     */
    private String getTextFromNode(Node node) {
        StringBuilder textBuilder = new StringBuilder();
        appendTextFromNode(node, textBuilder);
        return textBuilder.toString();
    }

    /**
     * Recursively appends text from a node and its children to a StringBuilder.
     *
     * @param node the node to extract text from
     * @param builder the StringBuilder to append text to
     */
    private void appendTextFromNode(Node node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        // Append text content for Text nodes
        if (node instanceof Text textNode) {
            builder.append(textNode.getChars());
        }

        // Process children recursively
        Node child = node.getFirstChild();
        while (child != null) {
            appendTextFromNode(child, builder);
            child = child.getNext();
        }
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
