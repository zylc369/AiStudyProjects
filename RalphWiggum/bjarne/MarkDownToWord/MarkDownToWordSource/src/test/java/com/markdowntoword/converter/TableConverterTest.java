package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TableConverter.
 */
class TableConverterTest {

    @TempDir
    Path tempDir;

    private XWPFDocument document;

    @AfterEach
    void cleanup() throws IOException {
        if (document != null) {
            document.close();
        }
    }

    private Parser createTableParser() {
        DataHolder options = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        return Parser.builder(options).build();
    }

    @Test
    void constructor_initializesDocument() throws IOException {
        XWPFDocument doc = new XWPFDocument();
        TableConverter converter = new TableConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TableConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addTableRow_withSingleCell_createsTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        converter.addTableRow(new String[]{"Single cell"});

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable table = tables.get(0);
        assertEquals(1, table.getNumberOfRows());

        XWPFTableRow row = table.getRow(0);
        assertEquals(1, row.getTableCells().size());
        assertEquals("Single cell", row.getCell(0).getText());
    }

    @Test
    void addTableRow_withMultipleCells_createsTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        converter.addTableRow(new String[]{"col1", "col2", "col3"});

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable table = tables.get(0);
        assertEquals(1, table.getNumberOfRows());

        XWPFTableRow row = table.getRow(0);
        assertEquals(3, row.getTableCells().size());
        assertEquals("col1", row.getCell(0).getText());
        assertEquals("col2", row.getCell(1).getText());
        assertEquals("col3", row.getCell(2).getText());
    }

    @Test
    void addTableRow_withEmptyString_createsTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        converter.addTableRow(new String[]{""});

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTableRow row = tables.get(0).getRow(0);
        assertEquals("", row.getCell(0).getText());
    }

    @Test
    void addTableRow_withMultipleRows_sequentially() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        converter.addTableRow(new String[]{"h1", "h2"});
        converter.addTableRow(new String[]{"d1", "d2"});
        converter.addTableRow(new String[]{"d3", "d4"});

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable table = tables.get(0);
        assertEquals(3, table.getNumberOfRows());

        assertEquals("h1", table.getRow(0).getCell(0).getText());
        assertEquals("h2", table.getRow(0).getCell(1).getText());
        assertEquals("d1", table.getRow(1).getCell(0).getText());
        assertEquals("d2", table.getRow(1).getCell(1).getText());
        assertEquals("d3", table.getRow(2).getCell(0).getText());
        assertEquals("d4", table.getRow(2).getCell(1).getText());
    }

    @Test
    void addTableRow_withNullCells_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addTableRow(null)
        );

        assertEquals("Cells array cannot be null", exception.getMessage());
    }

    @Test
    void addTableRow_withNullCellInArray_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addTableRow(new String[]{"valid", null, "valid"})
        );

        assertEquals("Cell at index 1 cannot be null", exception.getMessage());
    }

    @Test
    void convertTable_withSimpleTable_createsWordTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| col1 | col2 |\n" +
            "|------|------|\n" +
            "| val1 | val2 |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable wordTable = tables.get(0);
        assertEquals(2, wordTable.getNumberOfRows());

        // Verify header row
        XWPFTableRow headerRow = wordTable.getRow(0);
        assertEquals(2, headerRow.getTableCells().size());
        assertEquals("col1", headerRow.getCell(0).getText());
        assertEquals("col2", headerRow.getCell(1).getText());

        // Verify data row
        XWPFTableRow dataRow = wordTable.getRow(1);
        assertEquals(2, dataRow.getTableCells().size());
        assertEquals("val1", dataRow.getCell(0).getText());
        assertEquals("val2", dataRow.getCell(1).getText());
    }

    @Test
    void convertTable_withTwoColumnsThreeRows_createsWordTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| col1 | col2 |\n" +
            "|------|------|\n" +
            "| val1 | val2 |\n" +
            "| val3 | val4 |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable wordTable = tables.get(0);
        assertEquals(3, wordTable.getNumberOfRows());
    }

    @Test
    void convertTable_withHeaderOnly_createsWordTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| col1 | col2 | col3 |\n" +
            "|------|------|------|"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable wordTable = tables.get(0);
        assertEquals(1, wordTable.getNumberOfRows());

        XWPFTableRow headerRow = wordTable.getRow(0);
        assertEquals(3, headerRow.getTableCells().size());
    }

    @Test
    void convertTable_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertTable(null)
        );

        assertEquals("Table cannot be null", exception.getMessage());
    }

    @Test
    void convertTable_withEmptyTable_createsWordTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("| val |\n|-----|");
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        // Should create at least a minimal table
        XWPFTable wordTable = tables.get(0);
        assertTrue(wordTable.getNumberOfRows() >= 1);
    }

    @Test
    void convertDocument_withMultipleTables_convertsAllTables() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "| h1 | h2 |\n" +
            "|----|----|\n" +
            "| d1 | d2 |\n\n" +
            "Some text\n\n" +
            "| a1 | a2 |\n" +
            "|----|----|\n" +
            "| b1 | b2 |\n"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFTable> tables = document.getTables();
        assertEquals(2, tables.size());

        // Verify first table
        XWPFTable firstTable = tables.get(0);
        assertEquals(2, firstTable.getNumberOfRows());
        assertEquals("h1", firstTable.getRow(0).getCell(0).getText());

        // Verify second table
        XWPFTable secondTable = tables.get(1);
        assertEquals(2, secondTable.getNumberOfRows());
        assertEquals("a1", secondTable.getRow(0).getCell(0).getText());
    }

    @Test
    void convertDocument_withNoTables_createsNoTables() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No tables here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFTable> tables = document.getTables();
        assertEquals(0, tables.size());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyTables() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n\n" +
            "| h1 | h2 |\n" +
            "|----|----|\n" +
            "| d1 | d2 |\n\n" +
            "More text here.\n\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable table = tables.get(0);
        assertEquals(2, table.getNumberOfRows());
        assertEquals("h1", table.getRow(0).getCell(0).getText());
        assertEquals("d1", table.getRow(1).getCell(0).getText());
    }

    @Test
    void headerCellsHaveBoldFormatting() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| header1 | header2 |\n" +
            "|---------|---------|\n" +
            "| data1   | data2   |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        XWPFTable wordTable = document.getTables().get(0);
        XWPFTableRow headerRow = wordTable.getRow(0);

        // Check header cell 1
        XWPFTableCell headerCell1 = headerRow.getCell(0);
        List<XWPFParagraph> paragraphs1 = headerCell1.getParagraphs();
        assertFalse(paragraphs1.isEmpty());
        List<XWPFRun> runs1 = paragraphs1.get(0).getRuns();
        assertFalse(runs1.isEmpty());
        assertTrue(runs1.get(0).isBold(), "Header cell 1 should have bold formatting");

        // Check header cell 2
        XWPFTableCell headerCell2 = headerRow.getCell(1);
        List<XWPFParagraph> paragraphs2 = headerCell2.getParagraphs();
        assertFalse(paragraphs2.isEmpty());
        List<XWPFRun> runs2 = paragraphs2.get(0).getRuns();
        assertFalse(runs2.isEmpty());
        assertTrue(runs2.get(0).isBold(), "Header cell 2 should have bold formatting");
    }

    @Test
    void dataCellsDoNotHaveBoldFormatting() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| header1 | header2 |\n" +
            "|---------|---------|\n" +
            "| data1   | data2   |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        XWPFTable wordTable = document.getTables().get(0);
        XWPFTableRow dataRow = wordTable.getRow(1);

        // Check data cell 1
        XWPFTableCell dataCell1 = dataRow.getCell(0);
        List<XWPFParagraph> paragraphs1 = dataCell1.getParagraphs();
        assertFalse(paragraphs1.isEmpty());
        List<XWPFRun> runs1 = paragraphs1.get(0).getRuns();
        assertFalse(runs1.isEmpty());
        assertFalse(runs1.get(0).isBold(), "Data cell 1 should not have bold formatting");

        // Check data cell 2
        XWPFTableCell dataCell2 = dataRow.getCell(1);
        List<XWPFParagraph> paragraphs2 = dataCell2.getParagraphs();
        assertFalse(paragraphs2.isEmpty());
        List<XWPFRun> runs2 = paragraphs2.get(0).getRuns();
        assertFalse(runs2.isEmpty());
        assertFalse(runs2.get(0).isBold(), "Data cell 2 should not have bold formatting");
    }

    @Test
    void cellContentPreserved_withSpecialCharacters() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| Special & < > \"' |\n" +
            "|-------------------|\n" +
            "| Normal           |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        XWPFTable wordTable = document.getTables().get(0);

        assertEquals("Special & < > \"'", wordTable.getRow(0).getCell(0).getText());
        assertEquals("Normal", wordTable.getRow(1).getCell(0).getText());
    }

    @Test
    void savedDocument_reopened_tableStructurePreserved() throws IOException {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| col1 | col2 |\n" +
            "|------|------|\n" +
            "| val1 | val2 |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        Path testFile = tempDir.resolve("test-table.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFTable> tables = reopenedDocument.getTables();
            assertEquals(1, tables.size());

            XWPFTable wordTable = tables.get(0);
            assertEquals(2, wordTable.getNumberOfRows());

            // Verify header row content
            assertEquals("col1", wordTable.getRow(0).getCell(0).getText());
            assertEquals("col2", wordTable.getRow(0).getCell(1).getText());

            // Verify data row content
            assertEquals("val1", wordTable.getRow(1).getCell(0).getText());
            assertEquals("val2", wordTable.getRow(1).getCell(1).getText());
        }
    }

    @Test
    void savedDocument_reopened_headerBoldFormattingPersists() throws IOException {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| Header |\n" +
            "|--------|\n" +
            "| Data   |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        Path testFile = tempDir.resolve("test-header-bold.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            XWPFTable wordTable = reopenedDocument.getTables().get(0);

            // Verify header bold formatting persists
            XWPFTableCell headerCell = wordTable.getRow(0).getCell(0);
            List<XWPFRun> headerRuns = headerCell.getParagraphs().get(0).getRuns();
            assertFalse(headerRuns.isEmpty());
            assertTrue(headerRuns.get(0).isBold(), "Header bold formatting should persist after save");

            // Verify data cell remains non-bold
            XWPFTableCell dataCell = wordTable.getRow(1).getCell(0);
            List<XWPFRun> dataRuns = dataCell.getParagraphs().get(0).getRuns();
            assertFalse(dataRuns.isEmpty());
            assertFalse(dataRuns.get(0).isBold(), "Data cell should remain non-bold after save");
        }
    }

    @Test
    void edgeCase_singleRow_singleColumn() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| value |\n" +
            "|-------|"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable wordTable = tables.get(0);
        assertEquals(1, wordTable.getNumberOfRows());
        assertEquals(1, wordTable.getRow(0).getTableCells().size());
    }

    @Test
    void edgeCase_singleRow_multipleColumns() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| col1 | col2 | col3 | col4 |\n" +
            "|------|------|------|------|"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable wordTable = tables.get(0);
        assertEquals(1, wordTable.getNumberOfRows());
        assertEquals(4, wordTable.getRow(0).getTableCells().size());
    }

    @Test
    void edgeCase_singleColumn_multipleRows() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| header |\n" +
            "|--------|\n" +
            "| val1   |\n" +
            "| val2   |\n" +
            "| val3   |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTable wordTable = tables.get(0);
        assertEquals(4, wordTable.getNumberOfRows());
        assertEquals(1, wordTable.getRow(0).getTableCells().size());
        assertEquals(1, wordTable.getRow(1).getTableCells().size());
    }

    @Test
    void addTableRow_withSpecialCharacters_createsTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        converter.addTableRow(new String[]{"Value & \"test\" <special>"});

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());

        XWPFTableRow row = tables.get(0).getRow(0);
        assertEquals("Value & \"test\" <special>", row.getCell(0).getText());
    }

    @Test
    void convertTable_withTableOnlyHeaderNoSeparator_createsWordTable() {
        document = new XWPFDocument();
        TableConverter converter = new TableConverter(document);

        Parser parser = createTableParser();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse(
            "| col1 | col2 |\n" +
            "|------|------|\n" +
            "| val1 | val2 |\n" +
            "| val3 | val4 |"
        );
        TableBlock table = (TableBlock) doc.getFirstChild();

        converter.convertTable(table);

        List<XWPFTable> tables = document.getTables();
        assertEquals(1, tables.size());
        assertEquals(3, tables.get(0).getNumberOfRows());
    }
}
