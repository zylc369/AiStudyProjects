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
import com.vladsch.flexmark.ast.ThematicBreak;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.ext.tables.TableHead;
import com.vladsch.flexmark.ext.tables.TableBody;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Path;

/**
 * PDFGenerator uses Apache PDFBox to generate PDF documents from a Markdown AST.
 *
 * <p>This class traverses a flexmark Document AST and creates a PDF document with
 * equivalent formatting and structure using Apache PDFBox.</p>
 *
 * <p>Currently supported elements:</p>
 * <ul>
 *   <li>Headings (levels 1-6) with proper font sizes</li>
 *   <li>Paragraphs with text content</li>
 *   <li>Text formatting (bold, italic, bold-italic)</li>
 *   <li>Clickable hyperlinks</li>
 *   <li>Lists (ordered and unordered) with nesting support</li>
 *   <li>Code blocks with monospace font and light gray background</li>
 *   <li>Blockquotes with italic text, left border, and light gray background</li>
 *   <li>Tables with borders and header row formatting</li>
 *   <li>Images with aspect ratio preservation</li>
 *   <li>Horizontal rules - TO BE IMPLEMENTED</li>
 * </ul>
 *
 * <p>This is a basic implementation that will be expanded in subsequent tasks to support
 * all Markdown elements with proper formatting.</p>
 */
public class PDFGenerator {

    private static final float MARGIN = 50;
    private static final float DEFAULT_FONT_SIZE = 12;
    private static final float LIST_INDENT = 20; // Indentation per nesting level for lists
    private static final float CODE_FONT_SIZE = 10; // Font size for code blocks
    private static final float CODE_BLOCK_PADDING = 5; // Padding around code blocks
    private static final float CODE_BLOCK_BACKGROUND_GRAY = 0.95f; // Light gray background for code blocks
    private static final float BLOCKQUOTE_INDENT = 20; // Left indentation for blockquotes
    private static final float BLOCKQUOTE_BORDER_WIDTH = 2; // Border line width in points
    private static final float BLOCKQUOTE_BACKGROUND_GRAY = 0.90f; // Light gray background for blockquotes
    private static final float BLOCKQUOTE_PADDING = 5; // Padding around blockquote text
    private static final float TABLE_CELL_PADDING = 5; // Padding within table cells
    private static final float TABLE_BORDER_WIDTH = 1; // Border line width for tables
    private static final float TABLE_ROW_HEIGHT = 20; // Row height for table rows
    private static final float TABLE_MIN_COLUMN_WIDTH = 50; // Minimum column width
    private static final float IMAGE_MAX_WIDTH = PDRectangle.A4.getWidth() - (2 * MARGIN); // Maximum image width
    private static final float IMAGE_MAX_HEIGHT = 400; // Maximum image height in points
    private static final float IMAGE_SPACING = 10; // Spacing before/after images

    /**
     * Generates a PDF document from a Markdown AST.
     *
     * @param ast The flexmark Document AST to traverse and convert
     * @param outputPath The path where the .pdf file will be created
     * @throws IOException if the file cannot be written
     * @throws IllegalArgumentException if outputPath is null
     */
    public void generate(Document ast, Path outputPath) throws IOException {
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        // Create a new PDF document
        try (PDDocument document = new PDDocument()) {
            // Add initial page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Create content stream for the page
            try (PDPageContentStream content = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                // Set starting position (top of page with margin)
                float yPosition = PDRectangle.A4.getHeight() - MARGIN;

                // Load standard fonts - all 4 Times font variants for text formatting support
                PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
                PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
                PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
                PDFont boldItalicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC);

                content.beginText();
                content.setFont(regularFont, DEFAULT_FONT_SIZE);
                content.newLineAtOffset(MARGIN, yPosition);

                // Traverse the AST and convert each node
                if (ast != null) {
                    for (Node node : ast.getChildren()) {
                        if (node instanceof Heading) {
                            yPosition = processHeading((Heading) node, document, page, content, yPosition, regularFont, boldFont, italicFont, boldItalicFont);
                        } else if (node instanceof Paragraph) {
                            yPosition = processParagraph((Paragraph) node, document, page, content, yPosition, regularFont, boldFont, italicFont, boldItalicFont);
                        } else if (node instanceof BulletList) {
                            yPosition = processBulletList((BulletList) node, document, page, content, yPosition, 0);
                        } else if (node instanceof OrderedList) {
                            yPosition = processOrderedList((OrderedList) node, document, page, content, yPosition, 0);
                        } else if (node instanceof FencedCodeBlock) {
                            yPosition = processCodeBlock((FencedCodeBlock) node, document, content, yPosition);
                        } else if (node instanceof BlockQuote) {
                            yPosition = processBlockQuote((BlockQuote) node, document, page, content, yPosition);
                        } else if (node instanceof TableBlock) {
                            yPosition = processTable((TableBlock) node, document, page, content, yPosition);
                        } else if (node instanceof ThematicBreak) {
                            // TO BE IMPLEMENTED in subsequent task
                            yPosition = processThematicBreak((ThematicBreak) node, document, content, yPosition);
                        } else if (node instanceof Image) {
                            // TO BE IMPLEMENTED in subsequent task
                            yPosition = processImage((Image) node, document, content, yPosition);
                        }
                    }
                }

                content.endText();
            }

            // Save the document to the output file
            document.save(outputPath.toFile());
        }
    }

    /**
     * Processes a Markdown heading node and adds it to the PDF document.
     *
     * @param heading The flexmark Heading node to process
     * @param document The PDF document
     * @param page The PDF page for adding annotations
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @param regularFont The regular font for text
     * @param boldFont The bold font for text
     * @param italicFont The italic font for text
     * @param boldItalicFont The bold-italic font for text
     * @return New Y position after writing heading
     * @throws IOException if writing fails
     */
    private float processHeading(Heading heading, PDDocument document, PDPage page,
                                  PDPageContentStream content, float yPosition,
                                  PDFont regularFont, PDFont boldFont, PDFont italicFont, PDFont boldItalicFont) throws IOException {
        int level = heading.getLevel();

        // Validate heading level
        if (level < 1 || level > 6) {
            level = 1;
        }

        // Set font size based on heading level (h1=24pt, h2=20pt, h3=16pt, h4=14pt, h5=12pt, h6=10pt)
        float fontSize = 24 - (level - 1) * 4;

        // Process inline content with formatting (bold, italic, etc.)
        // Note: Word wrapping not implemented - text may overflow if too long
        yPosition = processInlineContent(heading, document, page, content, MARGIN, yPosition, fontSize,
                                         regularFont, boldFont, italicFont, boldItalicFont, false, false);

        // Add extra spacing after heading
        yPosition -= 4;

        return yPosition;
    }

    /**
     * Processes a Markdown paragraph node and adds it to the PDF document.
     *
     * @param paragraph The flexmark Paragraph node to process
     * @param document The PDF document
     * @param page The PDF page for adding annotations
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @param regularFont The regular font for text
     * @param boldFont The bold font for text
     * @param italicFont The italic font for text
     * @param boldItalicFont The bold-italic font for text
     * @return New Y position after writing paragraph
     * @throws IOException if writing fails
     */
    private float processParagraph(Paragraph paragraph, PDDocument document, PDPage page,
                                    PDPageContentStream content, float yPosition,
                                    PDFont regularFont, PDFont boldFont, PDFont italicFont, PDFont boldItalicFont) throws IOException {
        // Check if paragraph has any content
        boolean hasChildren = false;
        for (Node child : paragraph.getChildren()) {
            hasChildren = true;
            break;
        }

        if (!hasChildren) {
            // Empty paragraph - just add spacing
            yPosition -= DEFAULT_FONT_SIZE + 2;
            return yPosition;
        }

        // Process inline content with formatting (bold, italic, etc.)
        // Note: Word wrapping not implemented - text may overflow if too long
        yPosition = processInlineContent(paragraph, document, page, content, MARGIN, yPosition, DEFAULT_FONT_SIZE,
                                         regularFont, boldFont, italicFont, boldItalicFont, false, false);

        // Add spacing after paragraph
        yPosition -= 4;

        return yPosition;
    }

    /**
     * Processes inline content within a block node (heading or paragraph).
     * Handles mixed content including plain text, emphasis (italic), strong (bold) formatting,
     * inline code, and clickable hyperlinks.
     *
     * @param parent The parent node containing inline content
     * @param document The PDF document
     * @param page The PDF page for adding link annotations
     * @param content The PDF content stream for writing
     * @param xPosition Current X position on page (for tracking text width)
     * @param yPosition Current Y position on page
     * @param fontSize Font size to use for text
     * @param regularFont The regular font
     * @param boldFont The bold font
     * @param italicFont The italic font
     * @param boldItalicFont The bold-italic font
     * @param inheritedBold Whether bold formatting is inherited from parent
     * @param inheritedItalic Whether italic formatting is inherited from parent
     * @return New Y position after writing content
     * @throws IOException if writing fails
     */
    private float processInlineContent(Node parent, PDDocument document, PDPage page,
                                       PDPageContentStream content, float xPosition, float yPosition, float fontSize,
                                       PDFont regularFont, PDFont boldFont, PDFont italicFont, PDFont boldItalicFont,
                                       boolean inheritedBold, boolean inheritedItalic) throws IOException {
        for (Node child : parent.getChildren()) {
            if (child instanceof Text) {
                // Plain text node
                Text textNode = (Text) child;
                String text = textNode.getChars().toString();

                if (!text.isEmpty()) {
                    // Select font based on inherited formatting state
                    PDFont currentFont = selectFont(inheritedBold, inheritedItalic,
                                                     regularFont, boldFont, italicFont, boldItalicFont);

                    content.setFont(currentFont, fontSize);
                    showTextSafely(text, content);

                    // Update X position by text width
                    float textWidth = calculateTextWidth(text, currentFont, fontSize);
                    xPosition += textWidth;
                }
            } else if (child instanceof Emphasis) {
                // Italic text (*text* or _text_)
                Emphasis emphasis = (Emphasis) child;

                // Check if this Emphasis is within a StrongEmphasis (bold-italic case)
                boolean parentIsStrong = (child.getParent() instanceof StrongEmphasis);

                if (parentIsStrong) {
                    // Bold-italic: both inheritedBold and italic should be true
                    float[] positions = processInlineContentWithX(emphasis, document, page, content, xPosition, yPosition, fontSize,
                                                                     regularFont, boldFont, italicFont, boldItalicFont, true, true);
                    xPosition = positions[0];
                    yPosition = positions[1];
                } else {
                    // Just italic
                    float[] positions = processInlineContentWithX(emphasis, document, page, content, xPosition, yPosition, fontSize,
                                                                     regularFont, boldFont, italicFont, boldItalicFont, inheritedBold, true);
                    xPosition = positions[0];
                    yPosition = positions[1];
                }
            } else if (child instanceof StrongEmphasis) {
                // Bold text (**text** or __text__)
                StrongEmphasis strong = (StrongEmphasis) child;
                float[] positions = processInlineContentWithX(strong, document, page, content, xPosition, yPosition, fontSize,
                                                                 regularFont, boldFont, italicFont, boldItalicFont, true, inheritedItalic);
                xPosition = positions[0];
                yPosition = positions[1];
            } else if (child instanceof Code) {
                // Inline code `code`
                Code codeNode = (Code) child;
                String codeText = codeNode.getChars().toString();

                if (!codeText.isEmpty()) {
                    // Use monospace font for inline code
                    PDFont courierFont = new PDType1Font(Standard14Fonts.FontName.COURIER);
                    content.setFont(courierFont, fontSize);
                    content.showText(codeText);

                    // Update X position
                    float textWidth = calculateTextWidth(codeText, courierFont, fontSize);
                    xPosition += textWidth;
                }
            } else if (child instanceof Link) {
                // Hyperlink [text](url) - create clickable link
                Link link = (Link) child;
                String url = link.getUrl().toString();

                // Extract link text content and calculate starting position
                float linkStartX = xPosition;

                // Set link color to blue
                // Note: Link color not set due to PDFBox 3.0 beginText/endText limitations

                // Process link content (which may contain formatting)
                float[] positions = processInlineContentWithX(link, document, page, content, xPosition, yPosition, fontSize,
                                                                 regularFont, boldFont, italicFont, boldItalicFont,
                                                                 inheritedBold, inheritedItalic);
                float linkEndX = positions[0];
                yPosition = positions[1];
                xPosition = linkEndX;

                // Restore text color to black

                // Create clickable link annotation
                float linkWidth = linkEndX - linkStartX;
                if (linkWidth > 0 && !url.isEmpty()) {
                    // Link rectangle: (x, y-fontSize, width, fontSize*1.2)
                    // Note: Y position is text baseline, so rectangle goes from baseline-fontSize to baseline
                    PDRectangle linkBounds = new PDRectangle(linkStartX, yPosition - fontSize, linkWidth, fontSize * 1.2f);

                    // Create link annotation
                    PDAnnotationLink linkAnnotation = new PDAnnotationLink();
                    linkAnnotation.setRectangle(linkBounds);

                    // Set link action
                    PDActionURI action = new PDActionURI(); action.setURI(url);
                    linkAnnotation.setAction(action);

                    // Add annotation to page
                    page.getAnnotations().add(linkAnnotation);
                }
            } else if (child instanceof Image) {
                // Image - skip inline images for now, will be handled by processImage
                // Images are typically block-level, not inline
            } else {
                // Recurse for other node types
                float[] positions = processInlineContentWithX(child, document, page, content, xPosition, yPosition, fontSize,
                                                                 regularFont, boldFont, italicFont, boldItalicFont,
                                                                 inheritedBold, inheritedItalic);
                xPosition = positions[0];
                yPosition = positions[1];
            }
        }

        // Move to next line after processing all inline content
        content.newLineAtOffset(0, -fontSize - 2);
        yPosition -= fontSize + 2;

        return yPosition;
    }

    /**
     * Processes inline content and returns both X and Y positions.
     * Helper method to track X position for link annotations.
     *
     * @param parent The parent node containing inline content
     * @param document The PDF document
     * @param page The PDF page for adding annotations
     * @param content The PDF content stream
     * @param xPosition Current X position
     * @param yPosition Current Y position
     * @param fontSize Font size
     * @param regularFont The regular font
     * @param boldFont The bold font
     * @param italicFont The italic font
     * @param boldItalicFont The bold-italic font
     * @param inheritedBold Inherited bold state
     * @param inheritedItalic Inherited italic state
     * @return Array containing [newX, newY] positions
     * @throws IOException if writing fails
     */
    private float[] processInlineContentWithX(Node parent, PDDocument document, PDPage page,
                                              PDPageContentStream content, float xPosition, float yPosition, float fontSize,
                                              PDFont regularFont, PDFont boldFont, PDFont italicFont, PDFont boldItalicFont,
                                              boolean inheritedBold, boolean inheritedItalic) throws IOException {
        for (Node child : parent.getChildren()) {
            if (child instanceof Text) {
                Text textNode = (Text) child;
                String text = textNode.getChars().toString();

                if (!text.isEmpty()) {
                    PDFont currentFont = selectFont(inheritedBold, inheritedItalic,
                                                     regularFont, boldFont, italicFont, boldItalicFont);
                    content.setFont(currentFont, fontSize);
                    showTextSafely(text, content);

                    float textWidth = calculateTextWidth(text, currentFont, fontSize);
                    xPosition += textWidth;
                }
            } else if (child instanceof Emphasis) {
                Emphasis emphasis = (Emphasis) child;
                boolean parentIsStrong = (child.getParent() instanceof StrongEmphasis);

                if (parentIsStrong) {
                    float[] positions = processInlineContentWithX(emphasis, document, page, content, xPosition, yPosition, fontSize,
                                                                     regularFont, boldFont, italicFont, boldItalicFont, true, true);
                    xPosition = positions[0];
                    yPosition = positions[1];
                } else {
                    float[] positions = processInlineContentWithX(emphasis, document, page, content, xPosition, yPosition, fontSize,
                                                                     regularFont, boldFont, italicFont, boldItalicFont, inheritedBold, true);
                    xPosition = positions[0];
                    yPosition = positions[1];
                }
            } else if (child instanceof StrongEmphasis) {
                StrongEmphasis strong = (StrongEmphasis) child;
                float[] positions = processInlineContentWithX(strong, document, page, content, xPosition, yPosition, fontSize,
                                                                 regularFont, boldFont, italicFont, boldItalicFont, true, inheritedItalic);
                xPosition = positions[0];
                yPosition = positions[1];
            } else if (child instanceof Code) {
                Code codeNode = (Code) child;
                String codeText = codeNode.getChars().toString();

                if (!codeText.isEmpty()) {
                    PDFont courierFont = new PDType1Font(Standard14Fonts.FontName.COURIER);
                    content.setFont(courierFont, fontSize);
                    content.showText(codeText);

                    float textWidth = calculateTextWidth(codeText, courierFont, fontSize);
                    xPosition += textWidth;
                }
            } else if (child instanceof Link) {
                // Nested link within formatted text
                Link link = (Link) child;
                String url = link.getUrl().toString();
                float linkStartX = xPosition;

                content.setNonStrokingColor(0f, 0f, 1f);

                float[] positions = processInlineContentWithX(link, document, page, content, xPosition, yPosition, fontSize,
                                                                 regularFont, boldFont, italicFont, boldItalicFont,
                                                                 inheritedBold, inheritedItalic);
                float linkEndX = positions[0];
                yPosition = positions[1];
                xPosition = linkEndX;

                content.setNonStrokingColor(0f, 0f, 0f);

                float linkWidth = linkEndX - linkStartX;
                if (linkWidth > 0 && !url.isEmpty()) {
                    PDRectangle linkBounds = new PDRectangle(linkStartX, yPosition - fontSize, linkWidth, fontSize * 1.2f);
                    PDAnnotationLink linkAnnotation = new PDAnnotationLink();
                    linkAnnotation.setRectangle(linkBounds);
                    PDActionURI action = new PDActionURI(); action.setURI(url);
                    linkAnnotation.setAction(action);
                    page.getAnnotations().add(linkAnnotation);
                }
            } else if (child instanceof Image) {
                // Skip images
            } else {
                float[] positions = processInlineContentWithX(child, document, page, content, xPosition, yPosition, fontSize,
                                                                 regularFont, boldFont, italicFont, boldItalicFont,
                                                                 inheritedBold, inheritedItalic);
                xPosition = positions[0];
                yPosition = positions[1];
            }
        }

        return new float[]{xPosition, yPosition};
    }

    /**
     * Selects the appropriate font based on bold and italic flags.
     *
     * @param isBold Whether text should be bold
     * @param isItalic Whether text should be italic
     * @param regularFont The regular font
     * @param boldFont The bold font
     * @param italicFont The italic font
     * @param boldItalicFont The bold-italic font
     * @return The appropriate font for the given formatting
     */
    private PDFont selectFont(boolean isBold, boolean isItalic,
                              PDFont regularFont, PDFont boldFont, PDFont italicFont, PDFont boldItalicFont) {
        if (isBold && isItalic) {
            return boldItalicFont;
        } else if (isBold) {
            return boldFont;
        } else if (isItalic) {
            return italicFont;
        } else {
            return regularFont;
        }
    }

    /**
     * Calculates the width of text in PDF points.
     *
     * @param text The text to measure
     * @param font The font to use for measurement
     * @param fontSize The font size
     * @return The width of the text in PDF points
     * @throws IOException if font measurement fails
     */
    private float calculateTextWidth(String text, PDFont font, float fontSize) throws IOException {
        try {
            return font.getStringWidth(text) * fontSize / 1000f;
        } catch (IllegalArgumentException e) {
            // Font doesn't support some characters (e.g., box-drawing, symbols)
            // Estimate width: character count × average character width
            // For monospace fonts like Courier, use a fixed ratio
            float avgCharWidth = fontSize * 0.6f; // Approximate for monospace fonts
            return text.length() * avgCharWidth;
        }
    }

    /**
     * Filters out characters that are not supported by standard PDF fonts.
     * Removes box-drawing characters and other special Unicode symbols.
     *
     * @param text The text to filter
     * @return Text with unsupported characters removed
     */
    private String filterUnsupportedCharacters(String text) {
        // Remove box-drawing characters (U+2500-U+257F)
        // and other common unsupported symbols
        StringBuilder filtered = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Keep ASCII characters, filter out box-drawing and special symbols
            if (c < 128 || (c > 127 && c < 256)) {
                filtered.append(c);
            }
            // Alternatively, replace with ASCII equivalents:
            // ├ -> |-
            // ─ -> -
            // │ -> |
            // But for now, just filter them out
        }
        return filtered.toString();
    }

    /**
     * Safely shows text in PDF, filtering out unsupported Unicode characters.
     *
     * @param text The text to show
     * @param content The content stream
     */
    private void showTextSafely(String text, PDPageContentStream content) throws IOException {
        try {
            content.showText(text);
        } catch (IllegalArgumentException e) {
            // Font doesn't support some characters (e.g., box-drawing, symbols)
            String filteredText = filterUnsupportedCharacters(text);
            if (!filteredText.isEmpty()) {
                content.showText(filteredText);
            }
        }
    }

    /**
     * Processes a Markdown bullet list node and adds it to the PDF document.
     *
     * @param bulletList The flexmark BulletList node to process
     * @param document The PDF document
     * @param page The PDF page for adding annotations
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @param indentLevel Current nesting level (0 for top-level lists)
     * @return New Y position after processing list
     * @throws IOException if writing fails
     */
    private float processBulletList(BulletList bulletList, PDDocument document, PDPage page,
                                    PDPageContentStream content, float yPosition, int indentLevel) throws IOException {
        // Load regular font for list items
        PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
        PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
        PDFont boldItalicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC);

        // Iterate through list items
        for (Node itemNode : bulletList.getChildren()) {
            if (itemNode instanceof BulletListItem) {
                BulletListItem listItem = (BulletListItem) itemNode;

                // Calculate X position based on indentation level
                float xPos = MARGIN + (indentLevel * LIST_INDENT);

                // Draw bullet symbol
                content.setFont(regularFont, DEFAULT_FONT_SIZE);
                content.newLineAtOffset(xPos - MARGIN, 0);  // Move to indented position
                content.showText("\u2022 ");  // Unicode bullet character

                // Calculate bullet width for text positioning
                float bulletWidth = calculateTextWidth("\u2022 ", regularFont, DEFAULT_FONT_SIZE);

                // Process list item content (paragraphs and nested lists)
                for (Node child : listItem.getChildren()) {
                    if (child instanceof Paragraph) {
                        Paragraph para = (Paragraph) child;
                        // Move cursor past the bullet
                        content.newLineAtOffset(bulletWidth, 0);
                        // Process paragraph content
                        yPosition = processInlineContent(para, document, page, content, xPos + bulletWidth, yPosition, DEFAULT_FONT_SIZE,
                                                         regularFont, boldFont, italicFont, boldItalicFont, false, false);
                        // Reset to left margin for next item
                        content.newLineAtOffset(MARGIN - xPos - bulletWidth, 0);
                    } else if (child instanceof BulletList) {
                        // Nested bullet list - recurse with increased indent
                        yPosition = processBulletList((BulletList) child, document, page, content, yPosition, indentLevel + 1);
                    } else if (child instanceof OrderedList) {
                        // Nested ordered list - recurse with increased indent
                        yPosition = processOrderedList((OrderedList) child, document, page, content, yPosition, indentLevel + 1);
                    }
                }

                // Add spacing between list items
                yPosition -= 2;
            }
        }

        return yPosition;
    }

    /**
     * Processes a Markdown ordered list node and adds it to the PDF document.
     *
     * @param orderedList The flexmark OrderedList node to process
     * @param document The PDF document
     * @param page The PDF page for adding annotations
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @param indentLevel Current nesting level (0 for top-level lists)
     * @return New Y position after processing list
     * @throws IOException if writing fails
     */
    private float processOrderedList(OrderedList orderedList, PDDocument document, PDPage page,
                                     PDPageContentStream content, float yPosition, int indentLevel) throws IOException {
        // Load regular font for list items
        PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
        PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
        PDFont boldItalicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC);

        // Track item number
        int itemNumber = 1;

        // Iterate through list items
        for (Node itemNode : orderedList.getChildren()) {
            if (itemNode instanceof OrderedListItem) {
                OrderedListItem listItem = (OrderedListItem) itemNode;

                // Calculate X position based on indentation level
                float xPos = MARGIN + (indentLevel * LIST_INDENT);

                // Draw number with period
                content.setFont(regularFont, DEFAULT_FONT_SIZE);
                content.newLineAtOffset(xPos - MARGIN, 0);  // Move to indented position
                String numberText = itemNumber + ". ";
                content.showText(numberText);

                // Calculate number width for text positioning
                float numberWidth = calculateTextWidth(numberText, regularFont, DEFAULT_FONT_SIZE);

                // Process list item content (paragraphs and nested lists)
                for (Node child : listItem.getChildren()) {
                    if (child instanceof Paragraph) {
                        Paragraph para = (Paragraph) child;
                        // Move cursor past the number
                        content.newLineAtOffset(numberWidth, 0);
                        // Process paragraph content
                        yPosition = processInlineContent(para, document, page, content, xPos + numberWidth, yPosition, DEFAULT_FONT_SIZE,
                                                         regularFont, boldFont, italicFont, boldItalicFont, false, false);
                        // Reset to left margin for next item
                        content.newLineAtOffset(MARGIN - xPos - numberWidth, 0);
                    } else if (child instanceof BulletList) {
                        // Nested bullet list - recurse with increased indent
                        yPosition = processBulletList((BulletList) child, document, page, content, yPosition, indentLevel + 1);
                    } else if (child instanceof OrderedList) {
                        // Nested ordered list - recurse with increased indent
                        yPosition = processOrderedList((OrderedList) child, document, page, content, yPosition, indentLevel + 1);
                    }
                }

                // Increment item number
                itemNumber++;

                // Add spacing between list items
                yPosition -= 2;
            }
        }

        return yPosition;
    }

    /**
     * Processes a Markdown code block node and adds it to the PDF document.
     *
     * @param codeBlock The flexmark FencedCodeBlock node to process
     * @param document The PDF document
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @return New Y position after processing code block
     * @throws IOException if writing fails
     */
    private float processCodeBlock(FencedCodeBlock codeBlock, PDDocument document,
                                    PDPageContentStream content, float yPosition) throws IOException {
        // Extract code content from FencedCodeBlock
        StringBuilder codeBuilder = new StringBuilder();
        for (Node child : codeBlock.getChildren()) {
            if (child instanceof Text) {
                codeBuilder.append(((Text) child).getChars());
            }
        }
        String code = codeBuilder.toString();

        // Handle empty code blocks
        if (code.trim().isEmpty()) {
            return yPosition;
        }

        // Load Courier font for monospace text
        PDFont courierFont = new PDType1Font(Standard14Fonts.FontName.COURIER);

        // Split code into lines to calculate dimensions
        String[] lines = code.split("\n");
        int lineCount = lines.length;

        // Calculate maximum line width for rectangle width
        float maxLineWidth = 0;
        for (String line : lines) {
            float lineWidth = calculateTextWidth(line, courierFont, CODE_FONT_SIZE);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        // Calculate code block height and position
        float codeBlockHeight = (lineCount * CODE_FONT_SIZE) + ((lineCount - 1) * 1) + (2 * CODE_BLOCK_PADDING);
        float codeBlockWidth = maxLineWidth + (2 * CODE_BLOCK_PADDING);
        float rectX = MARGIN - CODE_BLOCK_PADDING;
        float rectY = yPosition - codeBlockHeight;

        // Save text state and draw background rectangle
        content.endText();
        content.setNonStrokingColor(CODE_BLOCK_BACKGROUND_GRAY, CODE_BLOCK_BACKGROUND_GRAY, CODE_BLOCK_BACKGROUND_GRAY);
        content.addRect(rectX, rectY, codeBlockWidth, codeBlockHeight);
        content.fill();

        // Restore text state
        content.beginText();
        content.setFont(courierFont, CODE_FONT_SIZE);
        content.newLineAtOffset(MARGIN - CODE_BLOCK_PADDING, yPosition - rectY - CODE_FONT_SIZE - CODE_BLOCK_PADDING);

        // Render code content line by line
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            try {
                content.showText(line);
            } catch (IllegalArgumentException e) {
                // Font doesn't support some characters (e.g., box-drawing, symbols)
                // Filter out unsupported characters and render what we can
                String filteredLine = filterUnsupportedCharacters(line);
                if (!filteredLine.isEmpty()) {
                    content.showText(filteredLine);
                }
            }

            // Move to next line (except for the last line)
            if (i < lines.length - 1) {
                content.newLineAtOffset(0, -CODE_FONT_SIZE - 1);
            }
        }

        // Update Y position after code block
        yPosition = rectY;

        // Reset color to black for subsequent text
        content.setNonStrokingColor(0f, 0f, 0f);

        // Add spacing after code block
        yPosition -= CODE_BLOCK_PADDING;

        return yPosition;
    }

    /**
     * Processes a Markdown blockquote node and adds it to the PDF document.
     *
     * @param blockQuote The flexmark BlockQuote node to process
     * @param document The PDF document
     * @param page The PDF page for annotations
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @return New Y position after processing blockquote
     * @throws IOException if writing fails
     */
    private float processBlockQuote(BlockQuote blockQuote, PDDocument document, PDPage page,
                                     PDPageContentStream content, float yPosition) throws IOException {
        // Load fonts for blockquote content
        PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
        PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
        PDFont boldItalicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC);

        // Calculate blockquote dimensions by iterating through paragraphs first
        float maxLineWidth = 0;
        float totalHeight = 0;
        int paragraphCount = 0;

        for (Node child : blockQuote.getChildren()) {
            if (child instanceof Paragraph) {
                Paragraph para = (Paragraph) child;
                String text = extractText(para);
                if (!text.trim().isEmpty()) {
                    float lineWidth = calculateTextWidth(text, regularFont, DEFAULT_FONT_SIZE);
                    if (lineWidth > maxLineWidth) {
                        maxLineWidth = lineWidth;
                    }
                    totalHeight += DEFAULT_FONT_SIZE + 2; // Font size + line spacing
                    paragraphCount++;
                }
            }
        }

        // Handle empty blockquotes
        if (paragraphCount == 0) {
            return yPosition;
        }

        // Calculate blockquote dimensions
        float blockquoteWidth = maxLineWidth + BLOCKQUOTE_INDENT + (2 * BLOCKQUOTE_PADDING);
        float blockquoteHeight = totalHeight + (2 * BLOCKQUOTE_PADDING);
        float rectX = MARGIN;
        float rectY = yPosition - blockquoteHeight;

        // Save text state and draw background rectangle
        content.endText();
        content.setNonStrokingColor(BLOCKQUOTE_BACKGROUND_GRAY, BLOCKQUOTE_BACKGROUND_GRAY, BLOCKQUOTE_BACKGROUND_GRAY);
        content.addRect(rectX, rectY, blockquoteWidth, blockquoteHeight);
        content.fill();

        // Draw left border line
        content.setStrokingColor(0.5f, 0.5f, 0.5f); // Medium gray
        content.setLineWidth(BLOCKQUOTE_BORDER_WIDTH);
        content.moveTo(MARGIN + BLOCKQUOTE_INDENT / 2, yPosition);
        content.lineTo(MARGIN + BLOCKQUOTE_INDENT / 2, rectY);
        content.stroke();
        content.setLineWidth(1.0f); // Reset to default

        // Restore text state
        content.beginText();
        content.setFont(italicFont, DEFAULT_FONT_SIZE);

        // Render blockquote content paragraph by paragraph
        float currentY = yPosition - BLOCKQUOTE_PADDING - DEFAULT_FONT_SIZE;
        for (Node child : blockQuote.getChildren()) {
            if (child instanceof Paragraph) {
                Paragraph para = (Paragraph) child;
                String text = extractText(para);
                if (!text.trim().isEmpty()) {
                    // Move to indented position
                    content.newLineAtOffset(MARGIN + BLOCKQUOTE_INDENT + BLOCKQUOTE_PADDING - MARGIN, currentY - yPosition);
                    // Process paragraph content with italic formatting
                    yPosition = processInlineContent(para, document, page, content, MARGIN + BLOCKQUOTE_INDENT + BLOCKQUOTE_PADDING, yPosition, DEFAULT_FONT_SIZE,
                                                     regularFont, boldFont, italicFont, boldItalicFont, false, true);
                }
            }
        }

        // Reset color to black for subsequent text
        content.setNonStrokingColor(0f, 0f, 0f);

        // Add spacing after blockquote
        yPosition = rectY - BLOCKQUOTE_PADDING;

        return yPosition;
    }

    /**
     * Processes a Markdown table node and adds it to the PDF document.
     *
     * @param table The flexmark TableBlock node to process
     * @param document The PDF document
     * @param page The PDF page for annotations
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @return New Y position after processing table
     * @throws IOException if writing fails
     */
    private float processTable(TableBlock table, PDDocument document, PDPage page,
                               PDPageContentStream content, float yPosition) throws IOException {
        // Load fonts for table content
        PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
        PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
        PDFont boldItalicFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC);

        // First pass: Collect all TableRow nodes from TableHead and TableBody
        java.util.List<TableRow> allRows = new java.util.ArrayList<>();
        for (Node child : table.getChildren()) {
            if (child instanceof TableRow) {
                allRows.add((TableRow) child);
            } else if (child instanceof TableHead || child instanceof TableBody) {
                for (Node rowNode : child.getChildren()) {
                    if (rowNode instanceof TableRow) {
                        allRows.add((TableRow) rowNode);
                    }
                }
            }
            // Skip TableSeparator
        }

        // Count rows and determine maximum columns
        int rowCount = allRows.size();
        int columnCount = 0;
        for (TableRow row : allRows) {
            int rowColumns = 0;
            for (Node cellNode : row.getChildren()) {
                if (cellNode instanceof TableCell) {
                    rowColumns++;
                }
            }
            columnCount = Math.max(columnCount, rowColumns);
        }

        // Handle empty tables
        if (rowCount == 0 || columnCount == 0) {
            return yPosition;
        }

        // Calculate column widths
        float[] columnWidths = new float[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnWidths[i] = TABLE_MIN_COLUMN_WIDTH;
        }

        // For each cell, calculate text width and update column width
        for (TableRow row : allRows) {
            int cellIndex = 0;
            for (Node cellNode : row.getChildren()) {
                if (cellNode instanceof TableCell) {
                    TableCell cell = (TableCell) cellNode;
                    String cellText = extractText(cell);
                    float textWidth = calculateTextWidth(cellText, regularFont, DEFAULT_FONT_SIZE);
                    if (textWidth + (2 * TABLE_CELL_PADDING) > columnWidths[cellIndex]) {
                        columnWidths[cellIndex] = textWidth + (2 * TABLE_CELL_PADDING);
                    }
                    cellIndex++;
                }
            }
        }

        // Calculate table dimensions
        float tableWidth = 0;
        for (float width : columnWidths) {
            tableWidth += width;
        }
        float tableHeight = rowCount * TABLE_ROW_HEIGHT;
        float tableX = MARGIN;
        float tableY = yPosition - tableHeight;

        // Save text state and draw table borders
        content.endText();
        content.setStrokingColor(0f, 0f, 0f); // Black borders
        content.setLineWidth(TABLE_BORDER_WIDTH);

        // Draw outer border
        content.addRect(tableX, tableY, tableWidth, tableHeight);
        content.stroke();

        // Draw horizontal lines between rows
        for (int i = 1; i < rowCount; i++) {
            float lineY = tableY + (i * TABLE_ROW_HEIGHT);
            content.moveTo(tableX, lineY);
            content.lineTo(tableX + tableWidth, lineY);
            content.stroke();
        }

        // Draw vertical lines between columns
        float currentX = tableX;
        for (int i = 0; i < columnCount - 1; i++) {
            currentX += columnWidths[i];
            content.moveTo(currentX, tableY);
            content.lineTo(currentX, tableY + tableHeight);
            content.stroke();
        }

        // Reset line width
        content.setLineWidth(1.0f);

        // Restore text state and render cell content
        content.beginText();

        // Process each row
        int rowIndex = 0;
        for (TableRow row : allRows) {
            float cellY = tableY + ((rowCount - rowIndex - 1) * TABLE_ROW_HEIGHT) - TABLE_CELL_PADDING - DEFAULT_FONT_SIZE;

            // Process each cell in the row
            int cellIndex = 0;
            float cellX = tableX + TABLE_CELL_PADDING;
            for (Node cellNode : row.getChildren()) {
                if (cellNode instanceof TableCell) {
                    TableCell cell = (TableCell) cellNode;

                    // Set font: bold for header row (first row), regular for others
                    if (rowIndex == 0) {
                        content.setFont(boldFont, DEFAULT_FONT_SIZE);
                    } else {
                        content.setFont(regularFont, DEFAULT_FONT_SIZE);
                    }

                    // Move to cell position
                    content.newLineAtOffset(cellX - MARGIN, cellY - yPosition);

                    // Process cell content (TableCell contains inline nodes directly)
                    yPosition = processInlineContent(cell, document, page, content, cellX, cellY, DEFAULT_FONT_SIZE,
                                                     regularFont, boldFont, italicFont, boldItalicFont, rowIndex == 0, false);

                    // Move to next cell
                    cellX += columnWidths[cellIndex];
                    cellIndex++;
                }
            }
            rowIndex++;
        }

        // Update Y position
        yPosition = tableY;

        return yPosition;
    }

    /**
     * Processes a Markdown horizontal rule node - placeholder for future implementation.
     *
     * @param thematicBreak The flexmark ThematicBreak node to process
     * @param document The PDF document
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @return New Y position after processing horizontal rule
     * @throws IOException if writing fails
     */
    private float processThematicBreak(ThematicBreak thematicBreak, PDDocument document,
                                       PDPageContentStream content, float yPosition) throws IOException {
        // Placeholder: skip horizontal rules for now
        // Will be implemented in subsequent task
        return yPosition;
    }

    /**
     * Processes a Markdown image node and embeds it in the PDF document.
     *
     * @param image The flexmark Image node to process
     * @param document The PDF document
     * @param content The content stream for writing
     * @param yPosition Current Y position on page
     * @return New Y position after processing image
     * @throws IOException if writing fails
     */
    private float processImage(Image image, PDDocument document,
                               PDPageContentStream content, float yPosition) throws IOException {
        // Extract image URL (file path)
        String imageUrl = image.getUrl().toString();

        // Extract alt text for accessibility and fallback
        String altText = image.getText().toString();
        if (altText.isEmpty()) {
            altText = imageUrl;
        }

        try {
            // Load image from file using PDFBox
            PDImageXObject pdImage = PDImageXObject.createFromFile(imageUrl, document);

            // Get original image dimensions
            float originalWidth = pdImage.getWidth();
            float originalHeight = pdImage.getHeight();

            // Calculate scaled dimensions to fit page
            float scaledWidth = originalWidth;
            float scaledHeight = originalHeight;

            // Scale down if image is too wide
            if (scaledWidth > IMAGE_MAX_WIDTH) {
                float scaleRatio = IMAGE_MAX_WIDTH / scaledWidth;
                scaledWidth = IMAGE_MAX_WIDTH;
                scaledHeight = scaledHeight * scaleRatio;
            }

            // Scale down if image is too tall
            if (scaledHeight > IMAGE_MAX_HEIGHT) {
                float scaleRatio = IMAGE_MAX_HEIGHT / scaledHeight;
                scaledHeight = IMAGE_MAX_HEIGHT;
                scaledWidth = scaledWidth * scaleRatio;
            }

            // Add spacing before image
            yPosition -= IMAGE_SPACING;

            // Check if we need a new page (simplified check - doesn't create new page in this implementation)
            // For full implementation, would need to recreate content stream which is complex
            if (yPosition - scaledHeight < MARGIN) {
                // Not enough space - image may overflow, but we'll still render it
                // A more complete implementation would create a new page here
            }

            // Draw image at current position (left-aligned at margin)
            // Note: Must be in text mode for drawImage to work properly
            content.drawImage(pdImage, MARGIN, yPosition - scaledHeight, scaledWidth, scaledHeight);

            // Update Y position
            yPosition -= scaledHeight + IMAGE_SPACING;

        } catch (IOException e) {
            // If image loading fails, draw alt text as placeholder
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC), DEFAULT_FONT_SIZE);
            content.newLineAtOffset(MARGIN, yPosition - DEFAULT_FONT_SIZE);
            content.showText("[Image: " + altText + "]");
            content.endText();

            // Update Y position for placeholder text
            yPosition -= DEFAULT_FONT_SIZE + IMAGE_SPACING;
        }

        return yPosition;
    }

    /**
     * Extracts text content from a node and its children recursively.
     * Used for basic text extraction without formatting.
     *
     * @param node The node to extract text from
     * @return The concatenated text content
     */
    private String extractText(Node node) {
        StringBuilder text = new StringBuilder();

        // If it's a Text node, append its content
        if (node instanceof Text) {
            text.append(((Text) node).getChars());
        } else {
            // Otherwise, recursively get text from children
            for (Node child : node.getChildren()) {
                text.append(extractText(child));
            }
        }

        return text.toString();
    }
}
