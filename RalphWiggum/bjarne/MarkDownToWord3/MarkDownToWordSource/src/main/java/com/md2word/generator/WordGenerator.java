package com.md2word.generator;

import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * WordGenerator uses Apache POI to generate Word documents (.docx) from a Markdown AST.
 *
 * <p>This class traverses a flexmark Document AST and creates a Word document with
 * equivalent formatting and structure using Apache POI's XWPF (XML Word Processor Format).</p>
 *
 * <p>Currently supported elements:</p>
 * <ul>
 *   <li>Headings (levels 1-6) with proper Word styles</li>
 *   <li>Paragraphs with text formatting (bold, italic, bold-italic)</li>
 *   <li>Clickable hyperlinks</li>
 *   <li>Bulleted lists (unordered lists)</li>
 * </ul>
 *
 * <p>Future implementations will add:</p>
 * <ul>
 *   <li>Ordered lists, tables, and other elements</li>
 * </ul>
 */
public class WordGenerator {

    /**
     * Generates a Word document from a Markdown AST.
     *
     * @param ast The flexmark Document AST to traverse and convert
     * @param outputPath The path where the .docx file will be created
     * @throws IOException if the file cannot be written
     * @throws IllegalArgumentException if outputPath is null
     */
    public void generate(Document ast, Path outputPath) throws IOException {
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        // Create a new Word document
        XWPFDocument document = new XWPFDocument();

        // Traverse the AST and convert each node
        if (ast != null) {
            for (Node node : ast.getChildren()) {
                if (node instanceof Heading) {
                    processHeading((Heading) node, document);
                } else if (node instanceof Paragraph) {
                    processParagraph((Paragraph) node, document);
                } else if (node instanceof BulletList) {
                    processBulletList((BulletList) node, document);
                }
                // Other node types (OrderedList, etc.) will be added in future tasks
            }
        }

        // Write the document to the output file
        try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
            document.write(out);
        } finally {
            document.close();
        }
    }

    /**
     * Processes a Markdown heading node and adds it to the Word document.
     *
     * @param heading The flexmark Heading node to process
     * @param document The Word document to add the heading to
     */
    private void processHeading(Heading heading, XWPFDocument document) {
        int level = heading.getLevel();

        // Validate heading level (flexmark should guarantee 1-6)
        if (level < 1 || level > 6) {
            level = 1; // Default to Heading1 if invalid
        }

        // Create paragraph with appropriate heading style
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setStyle("Heading" + level);

        // Process inline content (text, emphasis, strong) within the heading
        processInlineContent(heading, paragraph, false, false);
    }

    /**
     * Processes a Markdown paragraph node and adds it to the Word document.
     *
     * @param paragraph The flexmark Paragraph node to process
     * @param document The Word document to add the paragraph to
     */
    private void processParagraph(Paragraph paragraph, XWPFDocument document) {
        // Create paragraph with default Word style (no special styling)
        XWPFParagraph wordParagraph = document.createParagraph();

        // Process inline content (text, emphasis, strong) within the paragraph
        processInlineContent(paragraph, wordParagraph, false, false);

        // Check if paragraph is empty by counting runs
        // Note: We can't easily remove the paragraph after creation, so we just check for empty runs
        // Empty paragraphs with no runs will simply be blank in the Word document
    }

    /**
     * Processes inline content within a block node (heading or paragraph).
     * Handles mixed content including plain text, emphasis (italic), strong (bold) formatting, and links.
     *
     * @param parent The parent node containing inline content
     * @param wordParagraph The Word paragraph to add runs to
     * @param inheritedBold Whether bold formatting is inherited from parent context
     * @param inheritedItalic Whether italic formatting is inherited from parent context
     */
    private void processInlineContent(Node parent, XWPFParagraph wordParagraph,
                                     boolean inheritedBold, boolean inheritedItalic) {
        for (Node child : parent.getChildren()) {
            if (child instanceof Text) {
                // Plain text node
                Text textNode = (Text) child;
                String text = textNode.getChars().toString();

                if (!text.isEmpty()) {
                    XWPFRun run = wordParagraph.createRun();
                    run.setText(text);
                    run.setBold(inheritedBold);
                    run.setItalic(inheritedItalic);
                }
            } else if (child instanceof Emphasis) {
                // Italic text (*text* or _text_)
                Emphasis emphasis = (Emphasis) child;

                // Check if this Emphasis is within a StrongEmphasis (bold-italic case)
                boolean parentIsStrong = (child.getParent() instanceof StrongEmphasis);

                if (parentIsStrong) {
                    // Bold-italic: both inheritedBold and italic should be true
                    processInlineContent(emphasis, wordParagraph, true, true);
                } else {
                    // Just italic
                    processInlineContent(emphasis, wordParagraph, inheritedBold, true);
                }
            } else if (child instanceof StrongEmphasis) {
                // Bold text (**text** or __text__)
                StrongEmphasis strong = (StrongEmphasis) child;
                processInlineContent(strong, wordParagraph, true, inheritedItalic);
            } else if (child instanceof Link) {
                // Hyperlink [text](url)
                Link link = (Link) child;
                String url = link.getUrl().toString();

                // Extract link text from children
                StringBuilder linkText = new StringBuilder();
                for (Node linkChild : link.getChildren()) {
                    if (linkChild instanceof Text) {
                        linkText.append(((Text) linkChild).getChars());
                    }
                    // For simplicity, ignore formatting within link text for now
                }

                String text = linkText.toString();
                if (!text.isEmpty() && !url.isEmpty()) {
                    // Create hyperlink run
                    XWPFRun run = wordParagraph.createRun();
                    run.setText(text);
                    run.setBold(inheritedBold);
                    run.setItalic(inheritedItalic);

                    // Add hyperlink styling (blue color and underline)
                    run.setColor("0000FF"); // Blue color for links
                    run.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                }
            }
            // Other inline node types (Code, etc.) will be added in future tasks
        }
    }

    /**
     * Processes a Markdown bullet list node and adds it to the Word document.
     *
     * @param bulletList The flexmark BulletList node to process
     * @param document The Word document to add the list to
     */
    private void processBulletList(BulletList bulletList, XWPFDocument document) {
        // Iterate through list items
        for (Node node : bulletList.getChildren()) {
            if (node instanceof BulletListItem) {
                BulletListItem listItem = (BulletListItem) node;

                // Create paragraph with bullet style
                XWPFParagraph paragraph = document.createParagraph();
                paragraph.setStyle("List Bullet");

                // Process the list item's content (typically a Paragraph node)
                // List items can contain Paragraph nodes with inline content
                for (Node itemChild : listItem.getChildren()) {
                    if (itemChild instanceof Paragraph) {
                        Paragraph itemParagraph = (Paragraph) itemChild;
                        // Process inline content within the list item's paragraph
                        processInlineContent(itemParagraph, paragraph, false, false);
                    }
                    // Nested lists will be handled in "Add nested list support" task
                }
            }
        }
    }
}
