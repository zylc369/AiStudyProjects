# MarkDownToWord

A tool to convert Markdown files to Word documents.

## Project Details

- **GroupId**: com.markdowntoword
- **ArtifactId**: MarkDownToWord
- **Version**: 1.0
- **Packaging**: jar

## Prerequisites

- JDK 17 or higher
- Maven 3.6 or higher

## Build Instructions

To build the project:

```bash
cd MarkDownToWordSource
mvn clean package
```

This will create an executable JAR file in the `target/` directory:
- `MarkDownToWord-1.0.jar` (executable JAR with dependencies)

## Usage

Navigate to the project directory and run the application:

```bash
cd MarkDownToWordSource
java -jar target/MarkDownToWord-1.0.jar <input.md> [output.docx]
```

### Examples

**Basic conversion with explicit output file:**

```bash
cd MarkDownToWordSource
java -jar target/MarkDownToWord-1.0.jar test.md output.docx
```

**Conversion with automatic output filename:**
If you don't specify an output file, the tool will generate one by replacing the `.md` extension with `.docx`:

```bash
cd MarkDownToWordSource
java -jar target/MarkDownToWord-1.0.jar test.md
# Outputs: test.docx
```

### Command Line Arguments

| Argument | Required | Description |
|----------|----------|-------------|
| `<input.md>` | Yes | Path to the input Markdown file to convert |
| `[output.docx]` | No | Path to the output Word document. If omitted, uses input filename with `.docx` extension |

### Error Handling

The tool provides clear error messages for common issues:

```bash
# Missing input file
java -jar target/MarkDownToWord-1.0.jar
# Error: No input file specified. Usage: java -jar MarkDownToWord-1.0.jar <input.md> [output.docx]

# File doesn't exist
java -jar target/MarkDownToWord-1.0.jar nonexistent.md
# Error: Input file does not exist: nonexistent.md

# File not readable
java -jar target/MarkDownToWord-1.0.jar /root/protected.md
# Error: Input file is not readable: /root/protected.md
```

## Dependencies

- **Apache POI 5.2.5** - Word document generation
  - `poi`: Core POI library
  - `poi-ooxml`: OOXML support for .docx format
- **Flexmark-java 0.64.8** - Markdown parsing (flexmark-all)
- **JUnit 5.10.0** - Testing framework

## Project Structure

```
MarkDownToWordSource/
├── src/
│   ├── main/java/com/markdowntoword/    # Main Java source code
│   └── test/java/com/markdowntoword/    # Test Java source code
├── testFiles/                            # Test files directory
├── target/                               # Build output directory
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## Testing

To run tests:

```bash
cd MarkDownToWordSource
mvn test
```

## Supported Markdown Formats

The tool supports the following Markdown elements:

- **Headings** (H1-H6): `# Heading 1` through `###### Heading 6`
- **Text Formatting**:
  - Bold: `**bold text**` or `__bold text__`
  - Italic: `*italic text*` or `_italic text_`
  - Bold + Italic: `***bold italic***` or `___bold italic___`
- **Links**: `[link text](url)`
- **Inline Code**: `` `code` ``
- **Code Blocks**: Fenced code blocks with triple backticks
- **Unordered Lists**: Bullet lists with `-`, `*`, or `+`
- **Ordered Lists**: Numbered lists with `1.`, `2.`, etc.
- **Nested Lists**: Up to 4 levels of nested bullet or numbered lists
- **Tables**: Markdown tables with support for:
  - Standard table syntax
  - Column alignment (left, center, right)
  - Inline formatting within table cells

## Examples

### Converting a Document

```bash
# Convert a Markdown file to Word
cd MarkDownToWordSource
java -jar target/MarkDownToWord-1.0.jar README.md README.docx
```

### Using Test Files

The project includes sample Markdown files in the `testFiles/` directory:

```bash
# Convert test file with various formats
cd MarkDownToWordSource
java -jar target/MarkDownToWord-1.0.jar test-formats.md formats.docx

# Convert table examples
java -jar target/MarkDownToWord-1.0.jar test-tables.md tables.docx
```

## Features

- [x] Project structure setup
- [x] Maven configuration
- [x] Markdown parsing (Flexmark-java)
- [x] Word document generation (Apache POI)
- [x] Heading conversion (H1-H6)
- [x] Text formatting (bold, italic, bold+italic)
- [x] Link conversion
- [x] Inline code conversion
- [x] Code block conversion
- [x] Unordered list conversion
- [x] Ordered list conversion
- [x] Nested list conversion (up to 4 levels)
- [x] Table conversion with alignment
- [x] Command-line interface
- [x] Comprehensive unit tests
- [x] Integration tests

## Troubleshooting

### "No input file specified" error
Ensure you provide the input file path as the first argument:
```bash
java -jar target/MarkDownToWord-1.0.jar input.md
```

### "Input file does not exist" error
Check that the file path is correct. Use absolute paths or ensure you're in the correct directory:
```bash
# Relative path from MarkDownToWordSource directory
java -jar target/MarkDownToWord-1.0.jar test.md

# Absolute path
java -jar target/MarkDownToWord-1.0.jar /full/path/to/input.md
```

### "Input file is not readable" error
Ensure you have read permissions for the input file. On Linux/Mac, check with:
```bash
ls -l input.md
```

### Build errors (Maven)
- Ensure you have JDK 17 or higher installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Clear Maven cache if dependencies fail: `mvn clean`
- Use `mvn clean install -U` to force update dependencies

### JAR file not found
- Make sure you've built the project first: `mvn clean package`
- The JAR file is located in `target/MarkDownToWord-1.0.jar`

## Documentation

- [SPECIFICATION.md](../specs/SPECIFICATION.md) - Detailed feature specifications
- [CONTEXT.md](../CONTEXT.md) - Project context and overview
- [idea.md](../idea.md) - Original requirements document

## License

[Add license information]
