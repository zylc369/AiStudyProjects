package com.markdowntoword.converter;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableBody;
import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.ext.tables.TableHead;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.util.List;

/**
 * Converter for adding Markdown-style tables to Word documents.
 * Converts Flexmark TableBlock AST nodes to Word tables using Apache POI's XWPFTable,
 * with proper support for table headers and cell content extraction.
 */
public class TableConverter {

    private final XWPFDocument document;

    /**
     * Creates a TableConverter that will add tables to the specified document.
     *
     * @param document the XWPFDocument to add tables to
     * @throws IllegalArgumentException if document is null
     */
    public TableConverter(XWPFDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        this.document = document;
    }

    /**
     * Adds a table row with the specified cell contents to the document.
     * This creates a new table if no table exists, or adds a row to the last table.
     *
     * @param cells the text content for each cell in the row
     * @throws IllegalArgumentException if cells is null or contains null elements
     */
    public void addTableRow(String[] cells) {
        if (cells == null) {
            throw new IllegalArgumentException("Cells array cannot be null");
        }
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] == null) {
                throw new IllegalArgumentException("Cell at index " + i + " cannot be null");
            }
        }

        List<XWPFTable> tables = document.getTables();
        XWPFTable table;

        if (tables.isEmpty()) {
            // Create a new table with one row
            table = document.createTable(1, cells.length);
        } else {
            // Add a new row to the last table
            table = tables.get(tables.size() - 1);
            XWPFTableRow row = table.createRow();
            // Ensure row has enough cells
            while (row.getTableCells().size() < cells.length) {
                row.createCell();
            }
            // Set cell contents
            for (int i = 0; i < cells.length; i++) {
                row.getCell(i).removeParagraph(0);
                XWPFParagraph paragraph = row.getCell(i).addParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(cells[i]);
            }
            return;
        }

        // Set cell contents for the first row (when creating new table)
        XWPFTableRow row = table.getRow(0);
        for (int i = 0; i < cells.length; i++) {
            XWPFTableCell cell = row.getCell(i);
            cell.removeParagraph(0);
            XWPFParagraph paragraph = cell.addParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(cells[i]);
        }
    }

    /**
     * Converts a Flexmark TableBlock AST node to a Word table.
     * This method extracts the table structure from the AST node,
     * including headers from TableHead and data rows from TableBody,
     * and creates a corresponding Word table.
     *
     * @param tableBlock the Flexmark TableBlock AST node to convert
     * @throws IllegalArgumentException if tableBlock is null
     */
    public void convertTable(TableBlock tableBlock) {
        if (tableBlock == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }

        // Find TableHead and TableBody among children
        TableHead tableHead = null;
        TableBody tableBody = null;
        Node child = tableBlock.getFirstChild();
        while (child != null) {
            if (child instanceof TableHead) {
                tableHead = (TableHead) child;
            } else if (child instanceof TableBody) {
                tableBody = (TableBody) child;
            }
            child = child.getNext();
        }

        // Count rows and columns
        int rowCount = countRows(tableHead, tableBody);
        int columnCount = countColumns(tableHead, tableBody);

        if (rowCount == 0 || columnCount == 0) {
            // Create an empty table
            document.createTable(1, 1);
            return;
        }

        // Create the Word table
        XWPFTable wordTable = document.createTable(rowCount, columnCount);

        // Process table header
        if (tableHead != null) {
            processTableRow(tableHead.getFirstChild(), wordTable, 0, true);
        }

        // Process table body
        if (tableBody != null) {
            int rowIndex = tableHead != null ? 1 : 0;
            Node bodyRow = tableBody.getFirstChild();
            while (bodyRow != null) {
                if (bodyRow instanceof TableRow) {
                    processTableRow(bodyRow, wordTable, rowIndex, false);
                    rowIndex++;
                }
                bodyRow = bodyRow.getNext();
            }
        }
    }

    /**
     * Converts a Flexmark Document AST to Word tables.
     * This method iterates through the document's children and converts
     * all TableBlock nodes to Word tables with proper structure and formatting.
     *
     * @param document the Flexmark Document AST to convert
     * @throws IllegalArgumentException if document is null
     */
    public void convertDocument(com.vladsch.flexmark.util.ast.Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(TableBlock.class, this::convertTable)
        );

        visitor.visit(document);
    }

    /**
     * Processes a single table row and populates the corresponding Word table row.
     *
     * @param rowNode the TableRow AST node
     * @param wordTable the Word table to populate
     * @param rowIndex the index of the row in the Word table
     * @param isHeader whether this is a header row
     */
    private void processTableRow(Node rowNode, XWPFTable wordTable, int rowIndex, boolean isHeader) {
        if (rowNode == null) {
            return;
        }

        XWPFTableRow wordRow = wordTable.getRow(rowIndex);
        int cellIndex = 0;
        Node cellNode = rowNode.getFirstChild();

        while (cellNode != null && cellIndex < wordRow.getTableCells().size()) {
            if (cellNode instanceof TableCell cell) {
                String cellText = getTextFromNode(cell);
                XWPFTableCell wordCell = wordRow.getCell(cellIndex);
                wordCell.removeParagraph(0);
                XWPFParagraph paragraph = wordCell.addParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(cellText);

                // Apply bold formatting to header cells
                if (isHeader) {
                    run.setBold(true);
                }
                cellIndex++;
            }
            cellNode = cellNode.getNext();
        }
    }

    /**
     * Counts the total number of rows in a table.
     *
     * @param tableHead the TableHead node (may be null)
     * @param tableBody the TableBody node (may be null)
     * @return the total number of rows
     */
    private int countRows(TableHead tableHead, TableBody tableBody) {
        int count = 0;

        if (tableHead != null) {
            count += countChildRows(tableHead);
        }

        if (tableBody != null) {
            count += countChildRows(tableBody);
        }

        return count;
    }

    /**
     * Counts the number of TableRow children in a node.
     *
     * @param parent the parent node
     * @return the number of TableRow children
     */
    private int countChildRows(Node parent) {
        int count = 0;
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child instanceof TableRow) {
                count++;
            }
            child = child.getNext();
        }
        return count;
    }

    /**
     * Counts the maximum number of columns in a table.
     *
     * @param tableHead the TableHead node (may be null)
     * @param tableBody the TableBody node (may be null)
     * @return the maximum number of columns across all rows
     */
    private int countColumns(TableHead tableHead, TableBody tableBody) {
        int maxColumns = 0;

        if (tableHead != null) {
            maxColumns = Math.max(maxColumns, countRowColumns(tableHead));
        }

        if (tableBody != null) {
            maxColumns = Math.max(maxColumns, countRowColumns(tableBody));
        }

        return maxColumns;
    }

    /**
     * Counts the number of columns in the first TableRow child of a node.
     *
     * @param parent the parent node
     * @return the number of columns in the first row
     */
    private int countRowColumns(Node parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child instanceof TableRow row) {
                int count = 0;
                Node cell = row.getFirstChild();
                while (cell != null) {
                    if (cell instanceof TableCell) {
                        count++;
                    }
                    cell = cell.getNext();
                }
                return count;
            }
            child = child.getNext();
        }
        return 0;
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
