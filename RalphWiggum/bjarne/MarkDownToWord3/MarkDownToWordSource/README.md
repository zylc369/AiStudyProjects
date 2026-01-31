# Markdown to Word Converter

A Java-based command-line tool that converts Markdown files (.md) to Word documents (.docx) with rich text formatting preserved.

## Features

The converter preserves the following Markdown elements when converting to Word:

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
java -jar target/md2word-1.0-SNAPSHOT.jar <input.md> <output.docx>
```

### Examples

Convert a README to Word document:
```bash
java -jar target/md2word-1.0-SNAPSHOT.jar README.md README.docx
```

Convert a Markdown file with nested lists:
```bash
java -jar target/md2word-1.0-SNAPSHOT.jar document.md output.docx
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

- **Tables** - GitHub Flavored Markdown tables require `flexmark-ext-tables` extension
- **Horizontal rules** - `---`, `***`, `___` require flexmark GFM extensions

These features may be added in future releases by updating `pom.xml` dependencies.

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

## Project Structure

```
MarkDownToWordSource/
├── pom.xml                          # Maven build configuration
├── README.md                        # This file
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/md2word/
│   │           ├── Main.java       # CLI entry point
│   │           ├── parser/
│   │           │   └── MarkdownParser.java  # Markdown parsing
│   │           └── generator/
│   │               └── WordGenerator.java  # Word document generation
│   └── test/
│       ├── java/
│       │   └── com/md2word/
│       │       ├── HeadingConversionTest.java
│       │       ├── TextFormattingTest.java
│       │       └── ListConversionTest.java
│       └── resources/
└── test-resources/
    └── samples/                     # Sample Markdown files for testing
```

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
