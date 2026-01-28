# Core Converter API Specification

## Main API: MarkdownConverter

### Public Interface

```java
package com.markdown.toword;

public class MarkdownConverter {

    /**
     * Converts a Markdown string to a Word document
     *
     * @param markdownContent The Markdown content as a string
     * @param outputPath The path where the .docx file will be created
     * @throws ConversionException if conversion fails
     */
    public void convert(String markdownContent, String outputPath);

    /**
     * Converts a Markdown file to a Word document
     *
     * @param markdownFilePath Path to the input .md file
     * @param outputPath The path where the .docx file will be created
     * @throws IOException if file operations fail
     * @throws ConversionException if conversion fails
     */
    public void convertFile(String markdownFilePath, String outputPath);
}
```

### CLI Entry Point

```java
package com.markdown.toword;

public class Main {
    public static void main(String[] args) {
        // Usage: java -jar markdown-to-word.jar <input.md> [output.docx]
        // If output not specified, uses input filename with .docx extension
    }
}
```

## Package Structure

```
com.markdown.toword/
├── MarkdownConverter.java       # Main converter class
├── Main.java                    # CLI entry point
├── model/
│   ├── ConversionContext.java   # Holds conversion state
│   └── ConversionException.java # Custom exception
├── parser/
│   └── MarkdownParser.java      # Parses Markdown to AST
└── converter/
    ├── HeaderConverter.java     # Converts headers
    ├── TextConverter.java       # Converts paragraphs and text formatting
    ├── ListConverter.java       # Converts lists
    ├── TableConverter.java      # Converts tables
    ├── CodeConverter.java       # Converts code blocks
    ├── LinkConverter.java       # Converts links
    └── ImageConverter.java      # Converts images
```

## Exception Handling

```java
package com.markdown.toword.model;

public class ConversionException extends Exception {
    public ConversionException(String message);
    public ConversionException(String message, Throwable cause);
}
```

## Design Decisions

1. **Streaming vs In-Memory**: Use in-memory processing for simplicity (Markdown files are typically small)
2. **Error Handling**: Fail-fast on parsing errors, but attempt recovery for formatting issues
3. **Thread Safety**: Converter instances are NOT thread-safe (create new instance per conversion)
4. **Resource Management**: Use try-with-resources for all file operations
