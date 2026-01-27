package com.markdowntoword;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for MarkdownToWordConverter.
 */
class MarkdownToWordConverterTest {

    @TempDir
    Path tempDir;

    @Test
    void testSimpleConversion() throws Exception {
        // Create a simple Markdown file
        String markdown = """
                # Test Document

                This is a **bold** text and this is *italic* text.

                ## Subheading

                - Item 1
                - Item 2
                - Item 3

                This is a `code` example.
                """;

        Path inputFile = tempDir.resolve("test.md");
        Path outputFile = tempDir.resolve("test.docx");

        Files.writeString(inputFile, markdown);

        // Convert
        MarkdownToWordConverter converter = new MarkdownToWordConverter();
        converter.convert(inputFile, outputFile);

        // Verify output file exists and is not empty
        assertThat(Files.exists(outputFile)).isTrue();
        assertThat(Files.size(outputFile)).isGreaterThan(0);
    }

    @Test
    void testParagraphConversion() throws Exception {
        String markdown = "This is a simple paragraph.";

        Path inputFile = tempDir.resolve("para.md");
        Path outputFile = tempDir.resolve("para.docx");

        Files.writeString(inputFile, markdown);

        MarkdownToWordConverter converter = new MarkdownToWordConverter();
        converter.convert(inputFile, outputFile);

        assertThat(Files.exists(outputFile)).isTrue();
        assertThat(Files.size(outputFile)).isGreaterThan(0);
    }

    @Test
    void testHeadersConversion() throws Exception {
        String markdown = """
                # Heading 1
                ## Heading 2
                ### Heading 3
                #### Heading 4
                ##### Heading 5
                ###### Heading 6
                """;

        Path inputFile = tempDir.resolve("headers.md");
        Path outputFile = tempDir.resolve("headers.docx");

        Files.writeString(inputFile, markdown);

        MarkdownToWordConverter converter = new MarkdownToWordConverter();
        converter.convert(inputFile, outputFile);

        assertThat(Files.exists(outputFile)).isTrue();
        assertThat(Files.size(outputFile)).isGreaterThan(0);
    }

    @Test
    void testCodeBlockConversion() throws Exception {
        String markdown = """
                ```java
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                ```
                """;

        Path inputFile = tempDir.resolve("code.md");
        Path outputFile = tempDir.resolve("code.docx");

        Files.writeString(inputFile, markdown);

        MarkdownToWordConverter converter = new MarkdownToWordConverter();
        converter.convert(inputFile, outputFile);

        assertThat(Files.exists(outputFile)).isTrue();
        assertThat(Files.size(outputFile)).isGreaterThan(0);
    }

    @Test
    void testTableConversion() throws Exception {
        String markdown = """
                | Header 1 | Header 2 | Header 3 |
                |----------|----------|----------|
                | Cell 1   | Cell 2   | Cell 3   |
                | Cell 4   | Cell 5   | Cell 6   |
                """;

        Path inputFile = tempDir.resolve("table.md");
        Path outputFile = tempDir.resolve("table.docx");

        Files.writeString(inputFile, markdown);

        MarkdownToWordConverter converter = new MarkdownToWordConverter();
        converter.convert(inputFile, outputFile);

        assertThat(Files.exists(outputFile)).isTrue();
        assertThat(Files.size(outputFile)).isGreaterThan(0);
    }

    @Test
    void testNonExistentInputFile() {
        Path inputFile = tempDir.resolve("nonexistent.md");
        Path outputFile = tempDir.resolve("output.docx");

        MarkdownToWordConverter converter = new MarkdownToWordConverter();

        assertThatThrownBy(() -> converter.convert(inputFile, outputFile))
                .isInstanceOf(MarkdownToWordConverter.ConversionException.class)
                .hasMessageContaining("Failed to read input file");
    }
}
