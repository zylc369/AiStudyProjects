# API Specification

## Main Converter Class

### Package: `com.markdowntoword`

### MarkdownToWordConverter

```java
public class MarkdownToWordConverter {
    /**
     * Converts a Markdown file to a Word document
     *
     * @param markdownFilePath  Path to input Markdown file (.md)
     * @param wordFilePath      Path to output Word document (.docx)
     * @throws IOException      If file reading/writing fails
     */
    public void convert(String markdownFilePath, String wordFilePath) throws IOException;
}
```

## CLI Interface

### Main Class: `com.markdowntoword.Main`

```
Usage: java -jar markdown-to-word.jar <input.md> <output.docx>

Arguments:
  input.md      Path to the input Markdown file
  output.docx   Path to the output Word document

Options:
  --help        Show help message

Exit Codes:
  0             Success
  1             Error (file not found, invalid format, etc.)
```

## Conversion Rules

### Supported Markdown Elements

| Markdown | Word Format |
|----------|-------------|
| `# H1` | Heading 1 style (16pt bold) |
| `## H2` | Heading 2 style (14pt bold) |
| `### H3` | Heading 3 style (13pt bold) |
| `#### H4` | Heading 4 style (12pt bold) |
| `##### H5` | Heading 5 style (11pt bold) |
| `###### H6` | Heading 6 style (10pt bold) |
| `**bold**` | Bold font weight |
| `*italic*` | Italic font style |
| `***bolditalic***` | Bold + Italic |
| `` `code` `` | Consolas/Courier font, 10pt |
| `[text](url)` | Hyperlink |
| `- item` | Bulleted list (bullet) |
| `1. item` | Numbered list (decimal) |
| `> quote` | Italic, left border |
| `---` | Horizontal line |
| `---` | Horizontal line |
| `| table |` | Word table with borders |
| `
![alt](url)
` | Embedded image |
| ```language\ncode\n``` | Code block with monospace font and border |

### Code Block Handling

Fenced code blocks (```) should be converted to:
- Paragraph with "Code Block" style
- Monospace font (Consolas or Courier New)
- Light gray background (#F5F5F5)
- Border around the block
- Preserve whitespace and line breaks

### Table Handling

Markdown tables convert to Word tables with:
- Auto-fit column widths
- Single border lines
- Header row with bold text
- Preserve cell alignment (left/center/right)

## Error Handling

### Input Validation
- Input file must exist
- Input file must have .md extension
- Output file must have .docx extension
- Output directory must be writable

### Error Messages
- `Error: Input file not found: <path>`
- `Error: Invalid input file format. Expected .md file`
- `Error: Cannot write to output location: <path>`
- `Error: Failed to parse Markdown: <details>`
- `Error: Failed to create Word document: <details>`
