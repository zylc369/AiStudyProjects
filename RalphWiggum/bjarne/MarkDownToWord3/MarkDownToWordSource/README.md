# Markdown to Word Converter

A Java-based tool that converts Markdown files to Word documents (.docx format) with rich text formatting.

## Features

- **Text Formatting**
  - Bold (`**bold**` or `__bold__`)
  - Italic (`*italic*` or `_italic_`)
  - Bold + Italic (`***bolditalic***`)
  - Strikethrough (`~~strikethrough~~`)
  - Inline code (`` `code` `` with monospace font)
  - Hyperlinks (`[text](url)` with blue underlined style)
  - Images (`![alt](url)` with placeholder)

- **Document Structure**
  - Headers (H1-H6 with Word heading styles)
  - Paragraphs
  - Blockquotes (indented with italic text)
  - Horizontal rules (`---`, `***`, `___`)
  - Fenced code blocks (``` or ~~~ with monospace font)
  - Indented code blocks

- **Lists**
  - Bulleted lists (unordered with `-` or `*`)
  - Numbered lists (ordered with `1.`)
  - Nested lists (up to 3 levels)

- **Tables**
  - Markdown tables convert to Word tables
  - Proper cell structure and borders

## Requirements

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher (for building from source)

## Building

Clone the repository and build with Maven:

```bash
cd MarkDownToWordSource
mvn clean package
```

This creates an executable JAR file at `target/markdown-to-word-1.0.0.jar`.

## Running

Convert a Markdown file to Word:

```bash
java -jar target/markdown-to-word-1.0.0.jar input.md output.docx
```

### Example

```bash
java -jar markdown-to-word-1.0.0.jar README.md README.docx
```

## Usage Examples

### Simple Markdown

**Input (`example.md`):**
```markdown
# My Document

This is a **bold** statement and this is *italic*.

## Features

- Item 1
- Item 2
  - Nested item

| Column 1 | Column 2 |
|----------|----------|
| Value 1  | Value 2  |
```

**Convert:**
```bash
java -jar markdown-to-word-1.0.0.jar example.md example.docx
```

## Project Structure

```
MarkDownToWordSource/
├── src/main/java/com/markdown/toword/
│   ├── Main.java                    # CLI entry point
│   ├── MarkdownConverter.java       # Main converter
│   ├── parser/
│   │   └── MarkdownParser.java      # Markdown parsing (flexmark)
│   ├── converter/
│   │   ├── InlineTextConverter.java # Bold, italic, links, code
│   │   ├── HeaderConverter.java     # H1-H6 headers
│   │   ├── ParagraphConverter.java  # Paragraphs
│   │   ├── ListConverter.java       # Bulleted and numbered lists
│   │   ├── CodeBlockConverter.java  # Fenced code blocks
│   │   ├── BlockQuoteConverter.java # Blockquotes
│   │   ├── ThematicBreakConverter.java # Horizontal rules
│   │   └── TableConverter.java      # Tables
│   └── model/
│       └── ConversionException.java # Custom exception
└── pom.xml                          # Maven configuration
```

## Technology Stack

- **Java 17** - Core language
- **Apache POI 5.2.5** - Word document generation (poi-ooxml)
- **flexmark-java 0.64.8** - Markdown parsing with extensions:
  - Strikethrough extension
  - Tables extension
- **Maven** - Build tool
- **JUnit 5 + AssertJ** - Testing (not yet implemented)

## Limitations

- **Images**: Currently displays placeholder text instead of embedding actual images
- **Hyperlinks**: Visual style only (blue underlined), not clickable in Word
- **Tests**: Unit tests and integration tests not yet implemented

## License

This project is provided as-is for educational and development purposes.
