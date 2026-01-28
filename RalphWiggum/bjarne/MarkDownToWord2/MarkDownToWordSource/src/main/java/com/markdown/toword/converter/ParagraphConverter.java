package com.markdown.toword.converter;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.Document;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Converter for Markdown paragraph elements to Word paragraphs.
 */
public class ParagraphConverter {

    /**
     * Converts paragraphs from a Markdown AST to a Word document.
     *
     * @param document The Word document to add paragraphs to
     * @param astDocument The Markdown AST document
     */
    public void convertParagraphs(XWPFDocument document, Document astDocument) {
        // Iterate through all nodes in the document
        Node node = astDocument.getFirstChild();
        while (node != null) {
            // Only process Paragraph nodes (skip Heading nodes as they're handled by HeaderConverter)
            if (node instanceof Paragraph && !(node instanceof Heading)) {
                convertParagraph(document, (Paragraph) node);
            }
            node = node.getNext();
        }
    }

    /**
     * Converts a single paragraph to Word format.
     *
     * @param document The Word document
     * @param paragraph The paragraph node to convert
     */
    private void convertParagraph(XWPFDocument document, Paragraph paragraph) {
        XWPFParagraph wordParagraph = document.createParagraph();
        XWPFRun run = wordParagraph.createRun();

        // Extract text content from the paragraph's child nodes
        StringBuilder text = new StringBuilder();
        Node child = paragraph.getFirstChild();
        while (child != null) {
            if (child instanceof Text textNode) {
                text.append(textNode.getChars());
            }
            child = child.getNext();
        }
        run.setText(text.toString());
    }
}
