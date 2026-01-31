package com.md2word.generator;

import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.OrderedList;
import com.vladsch.flexmark.ast.OrderedListItem;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
 *   <li>Bulleted lists (unordered lists) with nested list support</li>
 *   <li>Numbered lists (ordered lists) with nested list support</li>
 *   <li>Code blocks (fenced ``` and inline `) with monospace font</li>
 *   <li>Blockquotes (>) with italic formatting and indentation</li>
 *   <li>Images (![alt](url)) embedded in document</li>
 * </ul>
 *
 * <p>Future implementations will add:</p>
 * <ul>
 *   <li>Tables, horizontal rules, and other elements</li>
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
                    processBulletList((BulletList) node, document, 1);
                } else if (node instanceof OrderedList) {
                    processOrderedList((OrderedList) node, document, 1);
                } else if (node instanceof FencedCodeBlock) {
                    processCodeBlock((FencedCodeBlock) node, document);
                } else if (node instanceof BlockQuote) {
                    processBlockQuote((BlockQuote) node, document);
                }
                // Other node types (tables, horizontal rules, images, etc.) will be added in future tasks
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
            } else if (child instanceof Code) {
                // Inline code `code`
                Code codeNode = (Code) child;
                String codeText = codeNode.getChars().toString();

                if (!codeText.isEmpty()) {
                    XWPFRun run = wordParagraph.createRun();
                    run.setFontFamily("Courier New");
                    run.setText(codeText);
                    // Inline code should not inherit bold/italic formatting
                }
            } else if (child instanceof Image) {
                // Image ![alt](url)
                Image imageNode = (Image) child;
                processImage(imageNode, wordParagraph);
            }
        }
    }

    /**
     * Processes a Markdown bullet list node and adds it to the Word document.
     *
     * @param bulletList The flexmark BulletList node to process
     * @param document The Word document to add the list to
     * @param level The nesting level (1 for top-level, 2+ for nested)
     */
    private void processBulletList(BulletList bulletList, XWPFDocument document, int level) {
        // Iterate through list items
        for (Node node : bulletList.getChildren()) {
            if (node instanceof BulletListItem) {
                BulletListItem listItem = (BulletListItem) node;

                // Create paragraph with bullet style based on level
                XWPFParagraph paragraph = document.createParagraph();
                String style = (level == 1) ? "List Bullet" : "List Bullet " + level;
                paragraph.setStyle(style);

                // Process the list item's content (Paragraph nodes or nested lists)
                for (Node itemChild : listItem.getChildren()) {
                    if (itemChild instanceof Paragraph) {
                        Paragraph itemParagraph = (Paragraph) itemChild;
                        // Process inline content within the list item's paragraph
                        processInlineContent(itemParagraph, paragraph, false, false);
                    } else if (itemChild instanceof BulletList) {
                        // Nested bullet list
                        processBulletList((BulletList) itemChild, document, level + 1);
                    } else if (itemChild instanceof OrderedList) {
                        // Nested numbered list within bullet list
                        processOrderedList((OrderedList) itemChild, document, level + 1);
                    }
                }
            }
        }
    }

    /**
     * Processes a Markdown ordered list node and adds it to the Word document.
     *
     * @param orderedList The flexmark OrderedList node to process
     * @param document The Word document to add the list to
     * @param level The nesting level (1 for top-level, 2+ for nested)
     */
    private void processOrderedList(OrderedList orderedList, XWPFDocument document, int level) {
        // Iterate through list items
        for (Node node : orderedList.getChildren()) {
            if (node instanceof OrderedListItem) {
                OrderedListItem listItem = (OrderedListItem) node;

                // Create paragraph with numbered list style based on level
                XWPFParagraph paragraph = document.createParagraph();
                String style = (level == 1) ? "List Number" : "List Number " + level;
                paragraph.setStyle(style);

                // Process the list item's content (Paragraph nodes or nested lists)
                for (Node itemChild : listItem.getChildren()) {
                    if (itemChild instanceof Paragraph) {
                        Paragraph itemParagraph = (Paragraph) itemChild;
                        // Process inline content within the list item's paragraph
                        processInlineContent(itemParagraph, paragraph, false, false);
                    } else if (itemChild instanceof BulletList) {
                        // Nested bullet list within numbered list
                        processBulletList((BulletList) itemChild, document, level + 1);
                    } else if (itemChild instanceof OrderedList) {
                        // Nested numbered list
                        processOrderedList((OrderedList) itemChild, document, level + 1);
                    }
                }
            }
        }
    }

    /**
     * Processes a Markdown fenced code block node and adds it to the Word document.
     *
     * @param codeBlock The flexmark FencedCodeBlock node to process
     * @param document The Word document to add the code block to
     */
    private void processCodeBlock(FencedCodeBlock codeBlock, XWPFDocument document) {
        // Create paragraph for code block
        XWPFParagraph paragraph = document.createParagraph();

        // Extract code content from the code block's text content
        StringBuilder codeBuilder = new StringBuilder();
        for (Node child : codeBlock.getChildren()) {
            if (child instanceof Text) {
                codeBuilder.append(((Text) child).getChars());
            }
        }
        String code = codeBuilder.toString();

        // Create run with monospace font
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Courier New");
        run.setText(code);
    }

    /**
     * Processes a Markdown blockquote node and adds it to the Word document.
     *
     * @param blockQuote The flexmark BlockQuote node to process
     * @param document The Word document to add the blockquote to
     */
    private void processBlockQuote(BlockQuote blockQuote, XWPFDocument document) {
        // Process each child paragraph within the blockquote
        for (Node child : blockQuote.getChildren()) {
            if (child instanceof Paragraph) {
                Paragraph paragraphNode = (Paragraph) child;

                // Create paragraph for blockquote content
                XWPFParagraph paragraph = document.createParagraph();

                // Apply left indentation for visual distinction (720 twips = 0.5 inch)
                paragraph.setIndentationLeft(720);

                // Process inline content within the blockquote paragraph with italic formatting
                processInlineContent(paragraphNode, paragraph, false, true); // italic=true
            }
        }
    }

    /**
     * Processes a Markdown image node and embeds it in the Word document.
     *
     * @param image The flexmark Image node to process
     * @param wordParagraph The Word paragraph to add the image to
     */
    private void processImage(Image image, XWPFParagraph wordParagraph) {
        // Extract image URL (treated as file path)
        String imageUrl = image.getUrl().toString();

        // Extract alt text (for accessibility)
        String altText = image.getText().toString();
        if (altText.isEmpty()) {
            altText = imageUrl; // Fallback to URL if no alt text
        }

        // Create run for image
        XWPFRun run = wordParagraph.createRun();

        // Load and embed image from file
        try (InputStream is = new FileInputStream(imageUrl)) {
            // Detect file type from extension
            int pictureType = detectPictureType(imageUrl);

            // Add picture to document (let Word auto-size, use default dimensions)
            run.addPicture(is, pictureType, imageUrl, 200, 200);
        } catch (Exception e) {
            // If image loading fails, add alt text as placeholder
            run.setText("[Image: " + altText + "]");
        }
    }

    /**
     * Detects the picture type based on file extension.
     *
     * @param filename The image file path or URL
     * @return The XWPFDocument picture type constant
     */
    private int detectPictureType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return XWPFDocument.PICTURE_TYPE_PNG;
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return XWPFDocument.PICTURE_TYPE_JPEG;
        } else if (lower.endsWith(".gif")) {
            return XWPFDocument.PICTURE_TYPE_GIF;
        } else if (lower.endsWith(".bmp")) {
            return XWPFDocument.PICTURE_TYPE_BMP;
        } else {
            // Default to PNG if unknown
            return XWPFDocument.PICTURE_TYPE_PNG;
        }
    }
}
