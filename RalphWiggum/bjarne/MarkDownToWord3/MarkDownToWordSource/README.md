# Markdown to Word and PDF Converter

A Java-based command-line tool that converts Markdown files (.md) to Word documents (.docx) and PDF documents (.pdf) with rich text formatting preserved.

## Features

The converter preserves the following Markdown elements when converting to Word or PDF:

- **Headings** (levels 1-6) with proper Word heading styles
- **Text formatting** (bold, italic, bold-italic combinations)
- **Clickable hyperlinks** with proper URL attributes
- **Lists** (ordered and unordered) including nested lists
- **Code blocks** (fenced and inline) with monospace font
- **Blockquotes** with italic formatting and indentation
- **Images** embedded from local file paths

## Prerequisites

- **JDK 17** or higher - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/)
- **Maven** 3.6+ - Download from [Maven](https://maven.apache.org/download.cgi)
- Alternatively, use SDKMAN to install both: `sdk install java 17.0.12-temp` and `sdk install maven`

Verify installations:
```bash
java -version
mvn -version
```

## Building

Clone or download the project source code, then navigate to the `MarkDownToWordSource` directory:

```bash
cd MarkDownToWordSource
```

Compile the project:
```bash
mvn clean compile
```

Package as executable JAR (optional):
```bash
mvn package
```

This will create `target/md2word-1.0-SNAPSHOT.jar`.

## Usage

### Basic Usage

After compilation, you can run the converter using:

```bash
java -cp target/classes com.md2word.Main <input.md> <output.docx>
```

### With JAR File

If you packaged the project with `mvn package`, you can use:

```bash
java -jar target/md2word-1.0-SNAPSHOT-jar-with-dependencies.jar <input.md> <output.docx>
```

Or for PDF output:

```bash
java -jar target/md2word-1.0-SNAPSHOT-jar-with-dependencies.jar <input.md> <output.pdf>
```

**Note**: The converter automatically detects the output format based on the file extension (.docx or .pdf).

### Examples

Convert a README to Word document:
```bash
java -jar target/md2word-1.0-SNAPSHOT-jar-with-dependencies.jar README.md README.docx
```

Convert a README to PDF document:
```bash
java -jar target/md2word-1.0-SNAPSHOT-jar-with-dependencies.jar README.md README.pdf
```

Convert a Markdown file with nested lists:
```bash
java -jar target/md2word-1.0-SNAPSHOT-jar-with-dependencies.jar document.md output.docx
```

Convert a Markdown file with nested lists to PDF:
```bash
java -jar target/md2word-1.0-SNAPSHOT-jar-with-dependencies.jar document.md document.pdf
```

## Supported Markdown Elements

| Element | Markdown Syntax | Word Output |
|---------|----------------|--------------|
| Heading 1 | `# Title` | Heading1 style |
| Heading 2 | `## Subtitle` | Heading2 style |
| Heading 3 | `### Section` | Heading3 style |
| Heading 4 | `#### Subsection` | Heading4 style |
| Heading 5 | `##### Sub-subsection` | Heading5 style |
| Heading 6 | `###### Paragraph` | Heading6 style |
| Bold | `**text**` or `__text__` | Bold text |
| Italic | `*text*` or `_text_` | Italic text |
| Bold Italic | `***text***` or `___text___` | Bold + Italic |
| Links | `[text](url)` | Clickable hyperlink |
| Unordered List | `- item` or `* item` or `+ item` | Bulleted list |
| Ordered List | `1. item` | Numbered list |
| Nested Lists | Indented items | Nested lists with proper hierarchy |
| Code Block | ```text``` | Monospace font, gray background |
| Inline Code | ``code`` | Monospace font |
| Blockquote | `> text` | Italic, indented, light background |
| Images | `![alt](path)` | Embedded image |
| Tables | GFM tables | Bordered tables with header row |
| Horizontal Rules | `---` | Horizontal line |

### PDF Output

The converter also supports PDF export with the following features:

| Element | Markdown Syntax | PDF Output |
|---------|----------------|------------|
| Heading 1 | `# Title` | 24pt font, bold |
| Heading 2 | `## Subtitle` | 20pt font, bold |
| Heading 3 | `### Section` | 16pt font, bold |
| Heading 4 | `#### Subsection` | 14pt font, bold |
| Heading 5 | `##### Sub-subsection` | 12pt font, bold |
| Heading 6 | `###### Paragraph` | 10pt font, bold |
| Bold | `**text**` or `__text__` | Bold text |
| Italic | `*text*` or `_text_` | Italic text |
| Bold Italic | `***text***` | Bold + Italic |
| Links | `[text](url)` | Clickable link (blue, underlined) |
| Unordered List | `- item` | Bulleted list with indentation |
| Ordered List | `1. item` | Numbered list with indentation |
| Code Block | ```text``` | Monospace font (Courier, 10pt), light gray background |
| Inline Code | ``code`` | Monospace font (Courier) |
| Blockquote | `> text` | Italic, left border, light gray background |
| Tables | GFM tables | Bordered tables, bold header row |
| Images | `![alt](path)` | Embedded images (scaled to fit page width) |
| Horizontal Rules | `---` | Horizontal line |
## Example

### Input Markdown (example.md)

```markdown
# My Document

This is a **bold** statement with *italic* emphasis.

## Features

- Easy to use
- Fast conversion
- Reliable output

### Code Example

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

> This is a blockquote with important information.
```

### Conversion Command

```bash
java -jar target/md2word-1.0-SNAPSHOT.jar example.md example.docx
```

## Limitations

The following Markdown features are **not currently supported** due to missing flexmark extension dependencies:

- **Task Lists** - GitHub Flavored Markdown task lists `[ ]` and `[x]`
- **Strikethrough** - `~~text~~` requires flexmark extension
- **Autolinks** - Automatic URL linking without explicit markdown syntax

**Note**: Tables and horizontal rules are now supported in the latest version!

## PDF Export Requirements

### Supported Features

The PDF export supports all the Markdown elements listed in the "Supported Markdown Elements" table above, including:

- All heading levels (1-6) with proper font size hierarchy
- Text formatting (bold, italic, bold-italic combinations)
- Clickable hyperlinks (blue, underlined)
- Lists (ordered and unordered) with proper indentation
- Nested lists (up to 3 levels)
- Code blocks with monospace font and light gray background
- Inline code with monospace font
- Blockquotes with italic text, left border, and light gray background
- Tables with borders and bold header row
- Images embedded from local file paths (scaled to fit page width)
- Horizontal rules (rendered as horizontal lines)

### Known Limitations

- **Unicode Characters**: Some Unicode characters may not render correctly in code blocks and will be filtered out. Use standard ASCII characters for best compatibility.
- **Font Support**: Uses standard PDF fonts (Times Roman for body, Courier for code). Custom fonts are not supported.
- **Page Layout**: Single-page layout. Automatic page breaks are not implemented.
- **Image Formats**: Supports common image formats (PNG, JPEG, GIF, BMP).
- **Table Cells**: Merged cells are not supported.

### Output Format

- **PDF Version**: 1.6
- **Page Size**: A4 (210mm × 297mm)
- **Margins**: 50pt (approximately 0.7 inches) on all sides
- **Fonts**:
  - Body text: Times Roman, 12pt
  - Headings: Times Roman Bold, 10-24pt (depending on level)
  - Code: Courier, 10pt (monospace)
  - Monospace: Courier, 10pt

## Troubleshooting

### "java: command not found"
- Ensure JDK is installed and in your PATH
- Set `JAVA_HOME` environment variable
- Add JDK bin directory to PATH

### "Unable to access jarfile"
- Ensure you've built the project with `mvn package`
- Verify the JAR file exists in `target/` directory
- Use `java -cp target/classes` method instead if you only compiled

### "Error: Input file does not exist"
- Check that the input Markdown file path is correct
- Use absolute paths if relative paths don't work
- Verify the file has read permissions

### "Build failed"
- Ensure you're using JDK 17 (run `java -version`)
- Check Maven is installed (`mvn -version`)
- Try `mvn clean` before compiling
- Check your internet connection (Maven needs to download dependencies)

### PDF Generation Issues

#### "Invalid argument - U+251C is not available in the font Courier"

**Problem**: Your Markdown contains unsupported Unicode characters in code blocks or diagrams.

**Solution**:
- Remove or replace these characters with ASCII equivalents
- For code blocks, use standard ASCII characters only
- Example: Replace tree diagrams with simple lists

#### PDF file is not created

**Problem**: The converter runs but no PDF file is generated.

**Solution**:
- Ensure the output filename ends with `.pdf` (case-sensitive)
- Check that the input Markdown file exists
- Verify you have write permissions in the output directory
- Look for error messages in the console output

#### Images are not appearing in PDF

**Problem**: Images referenced in Markdown don't show up in the PDF.

**Solution**:
- Ensure image paths are relative to the Markdown file location
- Verify image files exist at specified paths
- Check image format is supported (PNG, JPEG, GIF, BMP)
- Use `file` command to verify image files are valid

#### "java.lang.NoClassDefFoundError: org/apache/pdfbox..."

**Problem**: Required PDFBox library is not found.

**Solution**:
- Ensure you built the project with `mvn package`
- Use the JAR with dependencies: `md2word-1.0-SNAPSHOT-jar-with-dependencies.jar`
- Verify the JAR file exists in `target/` directory

## Project Structure

The project has the following structure:

- **src/main/java/com/md2word/**: Source code
  - `Main.java`: CLI entry point
  - `parser/MarkdownParser.java`: Markdown parsing
  - `generator/WordGenerator.java`: Word document generation
  - `generator/PDFGenerator.java`: PDF document generation
- **src/test/java/com/md2word/**: Unit tests
  - `HeadingConversionTest.java`: Heading conversion tests
  - `TextFormattingTest.java`: Text formatting tests
  - `ListConversionTest.java`: List conversion tests
  - `PDFConversionTest.java`: PDF conversion tests
- **pom.xml**: Maven build configuration
- **README.md**: This file

For detailed documentation, see the inline comments in the source code.

## Development

### Running Tests

Execute the test suite:

```bash
mvn test
```

### Clean Build

Remove all build artifacts:

```bash
mvn clean
```

### Compile Only

Compile without running tests:

```bash
mvn compile
```

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Version History

- **1.0-SNAPSHOT** - Initial release with core Markdown features
