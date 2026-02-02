package com.md2word;

import com.md2word.generator.WordGenerator;
import com.md2word.parser.MarkdownParser;
import com.vladsch.flexmark.util.ast.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Markdown table conversion to Word documents.
 *
 * <p>Tests verify that Markdown tables (GFM format) are correctly converted to Word documents
 * with proper table structure, cell content, and header row styling.</p>
 */
@DisplayName("Table Conversion Tests")
public class TableConversionTest {

    private MarkdownParser parser;
    private WordGenerator generator;

    @BeforeEach
    void setUp() {
        parser = new MarkdownParser();
        generator = new WordGenerator();
    }

    @Test
    @DisplayName("Simple table should convert with header and body rows")
    void testSimpleTable(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Header 1 | Header 2 |
            |----------|----------|
            | Cell 1   | Cell 2   |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            assertEquals(1, tables.size(), "Document should contain one table");

            XWPFTable table = tables.get(0);
            assertEquals(2, table.getRows().size(), "Table should have 2 rows (header + body)");

            // Check header row
            XWPFTableRow headerRow = table.getRow(0);
            assertEquals(2, headerRow.getTableCells().size(), "Header row should have 2 cells");
            assertTrue(headerRow.getCell(0).getText().contains("Header 1"), "First header cell should contain 'Header 1'");
            assertTrue(headerRow.getCell(1).getText().contains("Header 2"), "Second header cell should contain 'Header 2'");

            // Check body row
            XWPFTableRow bodyRow = table.getRow(1);
            assertEquals(2, bodyRow.getTableCells().size(), "Body row should have 2 cells");
            assertTrue(bodyRow.getCell(0).getText().contains("Cell 1"), "First body cell should contain 'Cell 1'");
            assertTrue(bodyRow.getCell(1).getText().contains("Cell 2"), "Second body cell should contain 'Cell 2'");
        }
    }

    @Test
    @DisplayName("Table with multiple columns should convert correctly")
    void testTableWithMultipleColumns(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | H1 | H2 | H3 | H4 |
            |----|----|----|----|
            | A  | B  | C  | D  |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            assertEquals(1, tables.size(), "Document should contain one table");

            XWPFTable table = tables.get(0);
            XWPFTableRow headerRow = table.getRow(0);
            assertEquals(4, headerRow.getTableCells().size(), "Header row should have 4 cells");

            assertTrue(headerRow.getCell(0).getText().contains("H1"), "Header 1 should be preserved");
            assertTrue(headerRow.getCell(1).getText().contains("H2"), "Header 2 should be preserved");
            assertTrue(headerRow.getCell(2).getText().contains("H3"), "Header 3 should be preserved");
            assertTrue(headerRow.getCell(3).getText().contains("H4"), "Header 4 should be preserved");

            XWPFTableRow bodyRow = table.getRow(1);
            assertTrue(bodyRow.getCell(0).getText().contains("A"), "Data should be preserved");
            assertTrue(bodyRow.getCell(1).getText().contains("B"), "Data should be preserved");
            assertTrue(bodyRow.getCell(2).getText().contains("C"), "Data should be preserved");
            assertTrue(bodyRow.getCell(3).getText().contains("D"), "Data should be preserved");
        }
    }

    @Test
    @DisplayName("Table with multiple rows should convert correctly")
    void testTableWithMultipleRows(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Name | Age |
            |------|-----|
            | Alice | 30 |
            | Bob | 25 |
            | Charlie | 35 |
            | David | 40 |
            | Eve | 28 |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            XWPFTable table = tables.get(0);
            assertEquals(6, table.getRows().size(), "Table should have 6 rows (header + 5 body rows)");

            XWPFTableRow headerRow = table.getRow(0);
            assertTrue(headerRow.getCell(0).getText().contains("Name"), "Header should be preserved");
            assertTrue(headerRow.getCell(1).getText().contains("Age"), "Header should be preserved");

            assertTrue(table.getRow(1).getCell(0).getText().contains("Alice"), "All rows should be preserved");
            assertTrue(table.getRow(2).getCell(0).getText().contains("Bob"), "All rows should be preserved");
            assertTrue(table.getRow(3).getCell(0).getText().contains("Charlie"), "All rows should be preserved");
            assertTrue(table.getRow(4).getCell(0).getText().contains("David"), "All rows should be preserved");
            assertTrue(table.getRow(5).getCell(0).getText().contains("Eve"), "All rows should be preserved");
        }
    }

    @Test
    @DisplayName("Table with empty cells should convert correctly")
    void testTableWithEmptyCells(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Header 1 | Header 2 | Header 3 |
            |----------|----------|----------|
            | Cell 1   |          | Cell 3   |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            XWPFTable table = tables.get(0);
            XWPFTableRow bodyRow = table.getRow(1);

            assertTrue(bodyRow.getCell(0).getText().contains("Cell 1"), "Non-empty cells should be preserved");
            assertTrue(bodyRow.getCell(1).getText().trim().isEmpty(), "Empty cells should be preserved");
            assertTrue(bodyRow.getCell(2).getText().contains("Cell 3"), "Non-empty cells should be preserved");
        }
    }

    @Test
    @DisplayName("Table header row should have bold formatting")
    void testTableHeaderStyling(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Name | Age |
            |------|-----|
            | Alice | 30 |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            XWPFTable table = tables.get(0);
            XWPFTableRow headerRow = table.getRow(0);

            // Verify header cells have bold formatting
            for (XWPFTableCell cell : headerRow.getTableCells()) {
                for (XWPFParagraph para : cell.getParagraphs()) {
                    for (XWPFRun run : para.getRuns()) {
                        assertTrue(run.isBold(), "Header row should have bold formatting");
                    }
                }
            }

            // Verify content is preserved
            assertTrue(headerRow.getCell(0).getText().contains("Name"), "Table content should be preserved");
            assertTrue(headerRow.getCell(1).getText().contains("Age"), "Table content should be preserved");
        }
    }

    @Test
    @DisplayName("Table content should be preserved correctly")
    void testTableContentPreservation(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Product | Price | Stock |
            |---------|-------|-------|
            | Widget  | $10.00 | 100 |
            | Gadget  | $25.00 | 50 |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            XWPFTable table = tables.get(0);
            XWPFTableRow headerRow = table.getRow(0);

            assertEquals("Product", headerRow.getCell(0).getText().trim(), "First header should match");
            assertEquals("Price", headerRow.getCell(1).getText().trim(), "Second header should be present");
            assertEquals("Stock", headerRow.getCell(2).getText().trim(), "Third header should be present");

            XWPFTableRow bodyRow1 = table.getRow(1);
            assertTrue(bodyRow1.getCell(0).getText().contains("Widget"), "Body data should be preserved");
            assertTrue(bodyRow1.getCell(1).getText().contains("$10.00"), "Body data should be preserved");
            assertTrue(bodyRow1.getCell(2).getText().contains("100"), "Body data should be preserved");

            XWPFTableRow bodyRow2 = table.getRow(2);
            assertTrue(bodyRow2.getCell(0).getText().contains("Gadget"), "Body data should be preserved");
        }
    }

    @Test
    @DisplayName("Document with multiple tables should convert correctly")
    void testMultipleTables(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | Table 1 Header |
            |---------------|
            | Table 1 Data  |

            | Table 2 Header |
            |---------------|
            | Table 2 Data  |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            assertEquals(2, tables.size(), "Document should contain two tables");

            XWPFTable table1 = tables.get(0);
            assertTrue(table1.getRow(0).getCell(0).getText().contains("Table 1 Header"), "First table header should be preserved");
            assertTrue(table1.getRow(1).getCell(0).getText().contains("Table 1 Data"), "First table data should be preserved");

            XWPFTable table2 = tables.get(1);
            assertTrue(table2.getRow(0).getCell(0).getText().contains("Table 2 Header"), "Second table header should be preserved");
            assertTrue(table2.getRow(1).getCell(0).getText().contains("Table 2 Data"), "Second table data should be preserved");
        }
    }

    @Test
    @DisplayName("Table with formatted text in cells should preserve formatting")
    void testTableWithFormatting(@TempDir Path tempDir) throws Exception {
        // Arrange
        String markdown = """
            | **Bold** | *Italic* | ***Bold Italic*** |
            |---------|---------|------------------|
            | **Data**| *Text*  | ***Both*** |
            """;
        Path outputFile = tempDir.resolve("output.docx");

        // Act
        Document document = parser.parse(markdown);
        generator.generate(document, outputFile);

        // Assert
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(outputFile.toFile()))) {
            var tables = doc.getTables();
            XWPFTable table = tables.get(0);

            // Check for formatted text in table cells
            boolean hasBoldRun = false;
            boolean hasItalicRun = false;

            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph para : cell.getParagraphs()) {
                        for (XWPFRun run : para.getRuns()) {
                            if (run.isBold()) hasBoldRun = true;
                            if (run.isItalic()) hasItalicRun = true;
                        }
                    }
                }
            }

            assertTrue(hasBoldRun, "Document should contain bold formatting");
            assertTrue(hasItalicRun, "Document should contain italic formatting");

            // Verify content is preserved
            XWPFTableRow headerRow = table.getRow(0);
            assertTrue(headerRow.getCell(0).getText().contains("Bold"), "Bold text should be preserved");
            assertTrue(headerRow.getCell(1).getText().contains("Italic"), "Italic text should be preserved");
        }
    }
}
