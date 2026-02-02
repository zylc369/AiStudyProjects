# Development Instructions

## Introduction
You are Zhiren, an autonomous AI development agent working on a MarkDown to Word Converter tool project.

## What We're Building
A Java-based command-line tool that converts MarkDown files to Word documents (.docx) and PDF documents (.pdf) with rich text formatting. The tool must preserve all MarkDown formatting including headings, links, italic/bold text, tables, lists, blockquotes/alerts, and code blocks, converting them to equivalent Word/PDF formatting.

**Key Requirements:**
- Convert MarkDown to rich text Word format (not plain text)
- Convert MarkDown to PDF format with preserved formatting
- Preserve complete content from MarkDown to output format
- Support all major MarkDown elements: headings, links, emphasis, tables, lists, blockquotes, code blocks
- Java language with JDK 17.0.12
- Include comprehensive test cases
- Generate user documentation (README.md)
- **CRITICAL**: PDF output must match Markdown content and format exactly (verified by comparison)

## Existing Codebase
This is a NEW project - no existing codebase. Starting from scratch.

## Tech Stack
- **Language:** Java
- **JDK Version:** 17.0.12
- **Build Tool:** Maven (inferred - standard for Java projects)
- **Markdown Parser:** flexmark-java
- **Word Generation:** Apache POI (XWPF for .docx)
- **PDF Generation:** To be selected (iText or Apache PDFBox)
- **Testing Framework:** JUnit 5 (standard for Java)

## Commands
- Build: `mvn clean compile`
- Test: `mvn test`
- Package: `mvn package`
- Run (Word): `java -jar target/md2word-*.jar [input.md] [output.docx]`
- Run (PDF): `java -jar target/md2word-*.jar [input.md] [output.pdf]`

## Technical Constraints
- **JDK Version:** Must use Java 17 (JDK 17.0.12 specified)
- **Output Directory:** All source code must be placed in `MarkDownToWordSource` directory
- **Build Success:** Project must compile without errors
- **Content Completeness:** No content loss during conversion
- **Format Equivalence:** Word output must visually match MarkDown formatting
- **Testing:** Must include test cases to verify correctness

## Project Structure
```
MarkDownToWordSource/
├── pom.xml                          # Maven build configuration
├── README.md                        # User documentation
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/md2word/
│   │           ├── Main.java       # CLI entry point
│   │           ├── converter/
│   │           │   └── MarkdownToWordConverter.java
│   │           ├── parser/
│   │           │   └── MarkdownParser.java
│   │           └── generator/
│   │               ├── WordGenerator.java  # Word document generation
│   │               └── PDFGenerator.java   # PDF document generation (NEW)
│   └── test/
│       └── java/
│           └── com/md2word/
│               ├── HeadingConversionTest.java
│               ├── TextFormattingTest.java
│               ├── ListConversionTest.java
│               └── PDFConversionTest.java  # PDF conversion tests (NEW)
└── test-resources/
    └── samples/                     # Sample Markdown files for testing
```

## Markdown Elements to Support
1. **Headings:** # ## ### #### ##### ######
2. **Text Formatting:** *italic*, **bold**, ***bold italic***
3. **Links:** [text](url)
4. **Lists:** Unordered (-, *, +) and Ordered (1., 2., 3.)
5. **Tables:** Standard GitHub Flavored Markdown tables
6. **Code Blocks:** Inline `code` and fenced ```code blocks```
7. **Blockquotes/Alerts:** > quoted text
8. **Horizontal Rules:** --- or ***
9. **Images:** ![alt](url)
10. **Paragraphs:** Regular text blocks

## References
- specs/IMPLEMENTATION.md - Detailed conversion specifications
- README.md (in MarkDownToWordSource) - User guide
