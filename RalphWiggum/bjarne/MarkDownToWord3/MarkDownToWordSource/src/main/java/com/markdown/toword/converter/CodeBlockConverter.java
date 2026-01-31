package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.CodeBlock;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Converter for code block elements (fenced and indented code blocks).
 */
public class CodeBlockConverter {

    /**
     * Converts a FencedCodeBlock node to Word paragraphs with monospace formatting.
     *
     * @param codeBlock The FencedCodeBlock node from the AST
     * @param document The Word document to add content to
     */
    public void convertFencedCodeBlock(FencedCodeBlock codeBlock, XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        // Set monospace font
        run.setFontFamily("Courier New");
        run.setFontSize(10);

        // Get the code content
        String code = codeBlock.getContentChars().toString();

        // Set the code text
        run.setText(code);

        // Optional: Add some spacing before/after
        paragraph.setSpacingBefore(100);
        paragraph.setSpacingAfter(100);
    }

    /**
     * Converts a CodeBlock node to Word paragraphs with monospace formatting.
     *
     * @param codeBlock The CodeBlock node from the AST
     * @param document The Word document to add content to
     */
    public void convertCodeBlock(CodeBlock codeBlock, XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        // Set monospace font
        run.setFontFamily("Courier New");
        run.setFontSize(10);

        // Get the code content
        String code = codeBlock.getContentChars().toString();

        // Set the code text
        run.setText(code);

        // Optional: Add some spacing before/after
        paragraph.setSpacingBefore(100);
        paragraph.setSpacingAfter(100);
    }

    /**
     * Converts an IndentedCodeBlock node to Word paragraphs with monospace formatting.
     *
     * @param codeBlock The IndentedCodeBlock node from the AST
     * @param document The Word document to add content to
     */
    public void convertIndentedCodeBlock(IndentedCodeBlock codeBlock, XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        // Set monospace font
        run.setFontFamily("Courier New");
        run.setFontSize(10);

        // Get the code content
        String code = codeBlock.getContentChars().toString();

        // Set the code text
        run.setText(code);

        // Optional: Add some spacing before/after
        paragraph.setSpacingBefore(100);
        paragraph.setSpacingAfter(100);
    }
}
