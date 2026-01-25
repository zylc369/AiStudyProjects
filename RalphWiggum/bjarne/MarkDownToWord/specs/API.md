# API Specification

## Command Line Interface

### Usage

```bash
java -jar markdown-to-word.jar <input.md> <output.docx>
```

### Arguments

| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| input.md | String | Yes | Path to input Markdown file |
| output.docx | String | Yes | Path to output Word document |

### Exit Codes

| Code | Meaning |
|------|---------|
| 0 | Success |
| 1 | File not found (input file doesn't exist) |
| 2 | Invalid file format (input not valid Markdown) |
| 3 | Write error (cannot create output file) |
| 4 | Internal error |

### Example

```bash
java -jar markdown-to-word.jar README.md README.docx
```

## Programmatic API

### MarkdownConverter

```java
package com.md2word;

public class MarkdownConverter {
    /**
     * Converts a Markdown file to a Word document.
     *
     * @param inputPath Path to input Markdown file
     * @param outputPath Path to output Word document
     * @throws IOException if file I/O fails
     * @throws MarkdownParseException if Markdown parsing fails
     */
    public void convert(String inputPath, String outputPath)
        throws IOException, MarkdownParseException;
}
```

### MarkdownParser

```java
package com.md2word.parser;

public class MarkdownParser {
    /**
     * Parses Markdown content into an AST.
     *
     * @param markdown Raw Markdown content
     * @return Root node of the AST
     */
    public MarkdownNode parse(String markdown);
}
```

### WordWriter

```java
package com.md2word.writer;

public class WordWriter {
    /**
     * Writes a Markdown AST to a Word document.
     *
     * @param root Root node of the Markdown AST
     * @param out Output stream for the Word document
     * @throws IOException if writing fails
     */
    public void write(MarkdownNode root, OutputStream out) throws IOException;
}
```

## Error Handling

All methods throw checked exceptions for error handling:

- `IOException`: File I/O errors
- `MarkdownParseException`: Invalid Markdown syntax
- `WordWriteException`: Word document generation errors
