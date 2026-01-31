package com.markdown.toword.converter;

import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.util.ast.Node;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.apache.poi.xwpf.usermodel.Borders;

/**
 * Converter for table elements.
 */
public class TableConverter {

    private final InlineTextConverter inlineTextConverter;

    public TableConverter() {
        this.inlineTextConverter = new InlineTextConverter();
    }

    /**
     * Converts a TableBlock node to a Word table.
     *
     * @param tableBlock The TableBlock node from the AST
     * @param document The Word document to add content to
     */
    public void convertTable(TableBlock tableBlock, XWPFDocument document) {
        // Count rows and columns
        int rowCount = 0;
        int colCount = 0;

        // First pass: count dimensions
        Node rowNode = tableBlock.getFirstChild();
        while (rowNode != null) {
            if (rowNode instanceof TableRow) {
                rowCount++;
                int rowCols = 0;
                Node cellNode = rowNode.getFirstChild();
                while (cellNode != null) {
                    if (cellNode instanceof TableCell) {
                        rowCols++;
                    }
                    cellNode = cellNode.getNext();
                }
                colCount = Math.max(colCount, rowCols);
            }
            rowNode = rowNode.getNext();
        }

        if (rowCount == 0 || colCount == 0) {
            return; // Empty table
        }

        // Create table in Word
        XWPFTable table = document.createTable(rowCount, colCount);

        // Set table borders
        setTableBorders(table);

        // Second pass: populate table
        int rowIndex = 0;
        rowNode = tableBlock.getFirstChild();
        while (rowNode != null) {
            if (rowNode instanceof TableRow row) {
                XWPFTableRow wordRow = table.getRow(rowIndex);
                if (wordRow != null) {
                    populateRow(wordRow, row);
                }
                rowIndex++;
            }
            rowNode = rowNode.getNext();
        }
    }

    /**
     * Populates a Word table row with content from a Markdown TableRow.
     *
     * @param wordRow The Word table row
     * @param markdownRow The Markdown TableRow node
     */
    private void populateRow(XWPFTableRow wordRow, TableRow markdownRow) {
        int colIndex = 0;
        Node cellNode = markdownRow.getFirstChild();
        while (cellNode != null) {
            if (cellNode instanceof TableCell cell) {
                if (colIndex < wordRow.getTableCells().size()) {
                    XWPFTableCell wordCell = wordRow.getCell(colIndex);
                    populateCell(wordCell, cell);
                    colIndex++;
                }
            }
            cellNode = cellNode.getNext();
        }
    }

    /**
     * Populates a Word table cell with content from a Markdown TableCell.
     *
     * @param wordCell The Word table cell
     * @param markdownCell The Markdown TableCell node
     */
    private void populateCell(XWPFTableCell wordCell, TableCell markdownCell) {
        // Clear existing paragraphs
        wordCell.getParagraphs().clear();

        // Create a paragraph for the cell content
        XWPFParagraph paragraph = wordCell.addParagraph();

        // Process inline content within the cell
        Node child = markdownCell.getFirstChild();
        while (child != null) {
            inlineTextConverter.processInlineNodes(paragraph, child);
            child = child.getNext();
        }

        // If no content, add empty text
        if (paragraph.getRuns().isEmpty()) {
            wordCell.setText("");
        }
    }

    /**
     * Sets borders on a Word table.
     *
     * @param table The Word table
     */
    private void setTableBorders(XWPFTable table) {
        // Set borders using POI Borders class
        table.setRowBandSize(1);
        table.setColBandSize(1);
        // Note: Full border customization requires CTTblBorders manipulation
        // This is a simplified approach
    }
}
