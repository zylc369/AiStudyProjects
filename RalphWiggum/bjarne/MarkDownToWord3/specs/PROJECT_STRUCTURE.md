# Project Structure Specification

## Directory Layout

```
MarkDownToWordSource/
├── pom.xml                                    # Maven build configuration
├── README.md                                  # User documentation
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── markdown/
│   │               └── toword/
│   │                   ├── MarkdownConverter.java         # Main converter class
│   │                   ├── Main.java                      # CLI entry point
│   │                   ├── converter/
│   │                   │   ├── HeaderConverter.java      # H1-H6 conversion
│   │                   │   ├── ParagraphConverter.java   # Paragraphs
│   │                   │   ├── InlineTextConverter.java  # Bold, italic, code, links
│   │                   │   ├── ListConverter.java        # Ordered/unordered lists
│   │                   │   ├── TableConverter.java       # Tables
│   │                   │   ├── CodeBlockConverter.java   # Fenced code blocks
│   │                   │   ├── BlockquoteConverter.java  # Blockquotes
│   │                   │   ├── ImageConverter.java       # Images
│   │                   │   └── HorizontalRuleConverter.java # Horizontal rules
│   │                   ├── parser/
│   │                   │   └── MarkdownParser.java      # Markdown to AST parser
│   │                   └── model/
│   │                       └── ConversionException.java  # Custom exception
│   └── test/
│       └── java/
│           └── com/
│               └── markdown/
│                   └── toword/
│                       ├── MarkdownParserTest.java
│                       ├── HeaderConverterTest.java
│                       ├── ParagraphConverterTest.java
│                       ├── InlineTextConverterTest.java
│                       ├── ListConverterTest.java
│                       ├── TableConverterTest.java
│                       ├── CodeBlockConverterTest.java
│                       ├── BlockquoteConverterTest.java
│                       └── MarkdownConverterTest.java    # Integration tests
└── samples/
    └── sample.md                            # Sample Markdown for testing
```

## Package Structure

### com.markdown.toword
Root package containing main converter class.

### com.markdown.toword.converter
All converter classes responsible for converting specific Markdown node types to Word format.

### com.markdown.toword.parser
Parser class for converting Markdown to AST.

### com.markdown.toword.model
Data models and custom exceptions.

## Class Responsibilities

### MarkdownConverter
- Main entry point for conversion
- Accepts Markdown string or file path
- Orchestrates all converters
- Handles output file writing

### MarkdownParser
- Wraps flexmark parser
- Returns AST Document

### Converter Classes (Base Pattern)
- Each converter handles one node type
- Methods accept `XWPFDocument` and AST node
- Traverse child nodes recursively when needed
- Use `InlineTextConverter` for inline formatting

### Main
- CLI entry point
- Parse command line arguments
- Call `MarkdownConverter.convertFile()`
- Handle errors and print user-friendly messages

## Maven Dependencies

```xml
<dependencies>
    <!-- Word document generation -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>

    <!-- Markdown parsing -->
    <dependency>
        <groupId>com.vladsch.flexmark</groupId>
        <artifactId>flexmark-all</artifactId>
        <version>0.64.8</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Build Configuration

- Maven compiler plugin: Java 17 source/target
- Maven surefire plugin: JUnit 5 test execution
- Package type: jar
- Final name: markdown-to-word-1.0.0
