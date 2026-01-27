package com.markdowntoword;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.ins.Ins;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Main converter class that transforms Markdown files into Word documents.
 * Supports rich text formatting including headers, bold, italic, tables, lists, code blocks, etc.
 */
public class MarkdownToWordConverter {

    private final Parser parser;
    private XWPFDocument document;
    private Path outputPath;

    public MarkdownToWordConverter() {
        // Initialize parser with extensions for tables, strikethrough, etc.
        this.parser = Parser.builder()
                .extensions(List.of(
                        org.commonmark.ext.gfm.tables.TablesExtension.create(),
                        org.commonmark.ext.ins.InsExtension.create(),
                        org.commonmark.ext.autolink.AutolinkExtension.create()
                ))
                .build();
        this.document = new XWPFDocument();
    }

    /**
     * Converts a Markdown file to a Word document.
     *
     * @param inputPath  Path to the input Markdown file
     * @param outputDocxPath Path to the output Word document
     * @throws ConversionException if conversion fails
     */
    public void convert(Path inputPath, Path outputDocxPath) throws ConversionException {
        try {
            String markdownContent = Files.readString(inputPath);
            convert(markdownContent, outputDocxPath);
        } catch (IOException e) {
            throw new ConversionException("Failed to read input file: " + inputPath, e);
        }
    }

    /**
     * Converts Markdown content to a Word document.
     *
     * @param markdownContent Markdown content as string
     * @param outputDocxPath  Path to the output Word document
     * @throws ConversionException if conversion fails
     */
    public void convert(String markdownContent, Path outputDocxPath) throws ConversionException {
        this.outputPath = outputDocxPath;

        try {
            // Parse Markdown into AST
            Node document = parser.parse(markdownContent);

            // Convert AST to Word document
            renderDocument(document);

            // Write Word document to file
            saveDocument();

        } catch (Exception e) {
            throw new ConversionException("Failed to convert Markdown to Word", e);
        }
    }

    /**
     * Renders the Markdown AST to the Word document.
     */
    private void renderDocument(Node markdownDocument) {
        NodeVisitor visitor = new NodeVisitor() {
            public void visit(Node node) {
                if (node instanceof Heading) {
                    renderHeading((Heading) node);
                } else if (node instanceof Paragraph) {
                    renderParagraph(node);
                } else if (node instanceof org.commonmark.node.BulletList) {
                    renderBulletList((org.commonmark.node.BulletList) node);
                } else if (node instanceof org.commonmark.node.OrderedList) {
                    renderOrderedList((org.commonmark.node.OrderedList) node);
                } else if (node instanceof TableBlock) {
                    renderTable((TableBlock) node);
                } else if (node instanceof org.commonmark.node.FencedCodeBlock) {
                    renderCodeBlock((org.commonmark.node.FencedCodeBlock) node);
                } else if (node instanceof org.commonmark.node.IndentedCodeBlock) {
                    renderIndentedCodeBlock((org.commonmark.node.IndentedCodeBlock) node);
                } else if (node instanceof org.commonmark.node.BlockQuote) {
                    renderBlockQuote(node);
                } else if (node instanceof org.commonmark.node.ThematicBreak) {
                    renderHorizontalRule();
                } else if (node instanceof org.commonmark.node.HtmlBlock) {
                    // Skip HTML blocks
                }

                // Visit children
                visitChildren(node);
            }

            public void visitChildren(Node node) {
                Node child = node.getFirstChild();
                while (child != null) {
                    Node next = child.getNext();
                    child.accept(this);
                    child = next;
                }
            }
        };

        markdownDocument.accept(visitor);
    }

    /**
     * Renders a heading element.
     */
    private void renderHeading(Heading heading) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();

        int level = heading.getLevel();
        String styleName = "Heading" + level;

        // Try to use built-in heading styles
        try {
            para.setStyle(styleName);
        } catch (Exception e) {
            // Fallback to manual formatting if style not available
            run.setBold(true);
            switch (level) {
                case 1:
                    run.setFontSize(24);
                    break;
                case 2:
                    run.setFontSize(20);
                    break;
                case 3:
                    run.setFontSize(16);
                    break;
                default:
                    run.setFontSize(14);
            }
        }

        renderInlineNodes(heading, run);
        para.setSpacingAfter(200);
    }

    /**
     * Renders a paragraph element.
     */
    private void renderParagraph(Node paragraph) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();
        renderInlineNodes(paragraph, run);
        para.setSpacingAfter(150);
    }

    /**
     * Renders inline formatting nodes within a run.
     */
    private void renderInlineNodes(Node parent, XWPFRun run) {
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child instanceof Text) {
                Text text = (Text) child;
                run.setText(text.getLiteral());
            } else if (child instanceof Emphasis) {
                XWPFRun newRun = createNewRun(run);
                newRun.setItalic(true);
                renderInlineNodes(child, newRun);
            } else if (child instanceof StrongEmphasis) {
                XWPFRun newRun = createNewRun(run);
                newRun.setBold(true);
                renderInlineNodes(child, newRun);
            } else if (child instanceof org.commonmark.node.Code) {
                XWPFRun newRun = createNewRun(run);
                newRun.setFontFamily("Courier New");
                String code = ((org.commonmark.node.Code) child).getLiteral();
                newRun.setText(code);
            } else if (child instanceof HardLineBreak) {
                run.addBreak();
            } else if (child instanceof SoftLineBreak) {
                run.addBreak();
            } else if (child instanceof Link) {
                Link link = (Link) child;
                XWPFRun newRun = createNewRun(run);
                newRun.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                newRun.setColor("0000FF");
                renderInlineNodes(child, newRun);
                // Store link destination in comment or create hyperlink
                try {
                    String href = link.getDestination();
                    createHyperlink(newRun, href);
                } catch (Exception e) {
                    // Fallback to underlined text
                }
            } else if (child instanceof org.commonmark.node.Image) {
                // Image handling - would need additional implementation
                renderInlineNodes(child, run);
            } else if (child instanceof Ins) {
                // Underline (for ++strikethrough++ extension)
                XWPFRun newRun = createNewRun(run);
                newRun.setUnderline(org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE);
                renderInlineNodes(child, newRun);
            } else {
                renderInlineNodes(child, run);
            }
            child = child.getNext();
        }
    }

    /**
     * Creates a new run in the same paragraph.
     */
    private XWPFRun createNewRun(XWPFRun originalRun) {
        XWPFParagraph para = originalRun.getParagraph();
        return para.createRun();
    }

    /**
     * Creates a hyperlink in the document.
     */
    private void createHyperlink(XWPFRun run, String url) {
        // Simple hyperlink implementation
        // For full implementation, would need to use CTHyperlink
        run.setText(url);
    }

    /**
     * Renders a bullet list.
     */
    private void renderBulletList(org.commonmark.node.BulletList list) {
        Node item = list.getFirstChild();
        while (item != null) {
            if (item instanceof org.commonmark.node.ListItem) {
                XWPFParagraph para = document.createParagraph();
                // Use bullet character for simple bullet list
                XWPFRun run = para.createRun();
                run.setText("• ");
                renderInlineNodes(item, run);
            }
            item = item.getNext();
        }
    }

    /**
     * Renders an ordered list.
     */
    private void renderOrderedList(org.commonmark.node.OrderedList list) {
        Node item = list.getFirstChild();
        while (item != null) {
            if (item instanceof org.commonmark.node.ListItem) {
                XWPFParagraph para = document.createParagraph();
                // For ordered lists, we'd need to create numbering definitions
                // For now, use simple text prefix
                XWPFRun run = para.createRun();
                renderInlineNodes(item, run);
            }
            item = item.getNext();
        }
    }

    /**
     * Renders a table.
     */
    private void renderTable(TableBlock table) {
        XWPFTable wordTable = document.createTable();

        // Set table width
        CTTblPr tblPr = wordTable.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = wordTable.getCTTbl().addNewTblPr();
        }
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(BigInteger.valueOf(9000));
        tblWidth.setType(STTblWidth.DXA);

        Node rowNode = table.getFirstChild();
        int rowIndex = 0;

        while (rowNode != null) {
            if (rowNode instanceof TableRow) {
                TableRow row = (TableRow) rowNode;

                // Get or create row
                XWPFTableRow wordRow;
                if (rowIndex < wordTable.getRows().size()) {
                    wordRow = wordTable.getRow(rowIndex);
                } else {
                    wordRow = wordTable.createRow();
                }

                Node cellNode = row.getFirstChild();
                int cellIndex = 0;

                while (cellNode != null) {
                    if (cellNode instanceof TableCell) {
                        // Add cell if needed
                        if (cellIndex >= wordRow.getTableCells().size()) {
                            wordRow.createCell();
                        }

                        XWPFTableCell cell = wordRow.getCell(cellIndex);
                        cell.removeParagraph(0);
                        XWPFParagraph para = cell.addParagraph();
                        XWPFRun run = para.createRun();

                        // Bold for header cells
                        if (rowNode.getParent() instanceof TableHead) {
                            run.setBold(true);
                        }

                        renderInlineNodes(cellNode, run);
                        cellIndex++;
                    }
                    cellNode = cellNode.getNext();
                }
                rowIndex++;
            }
            rowNode = rowNode.getNext();
        }
    }

    /**
     * Renders a fenced code block.
     */
    private void renderCodeBlock(org.commonmark.node.FencedCodeBlock codeBlock) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();

        run.setFontFamily("Courier New");
        run.setFontSize(10);
        run.setText(codeBlock.getLiteral());

        para.setSpacingAfter(150);
    }

    /**
     * Renders an indented code block.
     */
    private void renderIndentedCodeBlock(org.commonmark.node.IndentedCodeBlock codeBlock) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();

        run.setFontFamily("Courier New");
        run.setFontSize(10);
        run.setText(codeBlock.getLiteral());

        para.setSpacingAfter(150);
    }

    /**
     * Renders a block quote.
     */
    private void renderBlockQuote(Node blockQuote) {
        XWPFParagraph para = document.createParagraph();
        para.setIndentationLeft(720); // 0.5 inch indent
        para.setIndentationRight(360);

        XWPFRun run = para.createRun();
        run.setItalic(true);
        renderInlineNodes(blockQuote, run);

        para.setSpacingAfter(150);
    }

    /**
     * Renders a horizontal rule.
     */
    private void renderHorizontalRule() {
        XWPFParagraph para = document.createParagraph();
        para.setBorderBottom(org.apache.poi.xwpf.usermodel.Borders.SINGLE);
        para.setSpacingAfter(200);
    }

    /**
     * Saves the Word document to the output file.
     */
    private void saveDocument() throws IOException {
        try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
            document.write(out);
        }
    }

    /**
     * Abstract visitor class for traversing the Markdown AST.
     */
    private abstract static class NodeVisitor extends AbstractVisitor {
        public abstract void visitChildren(Node node);
    }

    /**
     * Exception thrown when conversion fails.
     */
    public static class ConversionException extends Exception {
        public ConversionException(String message) {
            super(message);
        }

        public ConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Main method for command-line usage.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar markdowntoword.jar <input.md> <output.docx>");
            System.out.println("Example: java -jar markdowntoword.jar README.md README.docx");
            System.exit(1);
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);

        if (!Files.exists(inputPath)) {
            System.err.println("Error: Input file does not exist: " + inputPath);
            System.exit(1);
        }

        try {
            MarkdownToWordConverter converter = new MarkdownToWordConverter();
            converter.convert(inputPath, outputPath);
            System.out.println("Successfully converted " + inputPath + " to " + outputPath);
        } catch (ConversionException e) {
            System.err.println("Error: Conversion failed - " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
