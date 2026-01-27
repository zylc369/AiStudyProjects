# MarkDown to Word Converter

A Java-based tool that converts Markdown files to Word (.docx) documents with rich text formatting.

## Features

This tool converts Markdown syntax to equivalent Word formatting, supporting:

- **Headers**: `#` through `######` for heading levels 1-6
- **Text Formatting**:
  - `**bold**` for bold text
  - `*italic*` for italic text
  - `` `inline code` `` for monospace text
- **Links**: `[text](url)` format
- **Lists**: Both ordered (1, 2, 3) and unordered (bullet) lists
- **Tables**: GitHub Flavored Markdown (GFM) tables
- **Code Blocks**: Fenced code blocks with language specification
- **Block Quotes**: `>` for quoted text
- **Horizontal Rules**: `---` for horizontal lines

## Requirements

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher (for building)

## Installation & Building

### Clone the repository

```bash
cd MarkDownToWordSource
```

### Build the project

```bash
mvn clean package
```

This will create an executable JAR file with all dependencies:
```
target/markdowntoword-converter-1.0.0-jar-with-dependencies.jar
```

## Usage

### Command Line

#### Basic Usage

```bash
java -jar target/markdowntoword-converter-1.0.0-jar-with-dependencies.jar <input.md> <output.docx>
```

#### Example

```bash
java -jar target/markdowntoword-converter-1.0.0-jar-with-dependencies.jar README.md README.docx
```

### Programmatic Usage

You can also use the converter in your Java code:

```java
import com.markdowntoword.MarkdownToWordConverter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example {
    public static void main(String[] args) {
        try {
            MarkdownToWordConverter converter = new MarkdownToWordConverter();

            Path inputPath = Paths.get("input.md");
            Path outputPath = Paths.get("output.docx");

            converter.convert(inputPath, outputPath);

            System.out.println("Conversion successful!");
        } catch (Exception e) {
            System.err.println("Conversion failed: " + e.getMessage());
        }
    }
}
```

## Examples

### Input Markdown (input.md)

```markdown
# Sample Document

This is a **bold** statement and this is *italic*.

## Features

- First item
- Second item
- Third item

### Code Example

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### Table

| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
```

### Output

The tool generates a Word document with:
- Properly styled headers (Heading 1, Heading 2, etc.)
- Bold and italic text formatting
- Bulleted lists with proper indentation
- Code blocks with monospace font
- Tables with borders and cell formatting

## Testing

Run the test suite:

```bash
mvn test
```

The tests cover:
- Simple conversion
- Paragraphs
- Headers (all levels)
- Code blocks
- Tables
- Error handling

## Project Structure

```
MarkDownToWordSource/
├── pom.xml                          # Maven build configuration
├── README.md                        # This file
├── src/
│   ├── main/
│   │   ├── java/com/markdowntoword/
│   │   │   └── MarkdownToWordConverter.java  # Main converter class
│   │   └── resources/
│   └── test/
│       ├── java/com/markdowntoword/
│       │   └── MarkdownToWordConverterTest.java  # Test cases
│       └── resources/
└── target/                          # Build output directory
```

## Technical Details

### Dependencies

- **CommonMark 0.21.0**: For parsing Markdown files
- **Apache POI 5.2.5**: For generating Word documents
- **JUnit 5.10.0**: For testing

### Implementation Notes

- Uses CommonMark parser with extensions for tables (GFM)
- Converts Markdown AST (Abstract Syntax Tree) to Word document structure
- Preserves all content and formatting from the original Markdown
- Supports UTF-8 encoding for international characters

## Limitations

- Strikethrough support is basic (underline used)
- Hyperlinks in Word are text-based (requires Word to recognize URLs)
- Image support is basic (requires full path or URL)
- Nested list formatting may need manual adjustment in Word

## Troubleshooting

### "Failed to read input file"
- Ensure the input Markdown file exists
- Check file permissions

### "Conversion failed"
- Verify the Markdown syntax is valid
- Check that the output directory is writable

### Compilation errors
- Ensure JDK 17 or higher is installed
- Verify Maven is properly configured

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Add tests for new features
4. Ensure all tests pass
5. Submit a pull request

## License

This project is provided as-is for educational and commercial use.

## Version History

- **1.0.0** (2026-01-25): Initial release
  - Core Markdown to Word conversion
  - Support for headers, text formatting, lists, tables, code blocks
  - Comprehensive test suite
