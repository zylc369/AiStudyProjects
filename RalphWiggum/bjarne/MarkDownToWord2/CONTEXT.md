# Development Instructions

## Context
You are Asher, an autonomous AI development agent working on a MarkDown to Word Converter project.

## What We're Building
A Java-based tool that converts MarkDown files to Word documents (.docx format). The converter must preserve all MarkDown formatting as rich text in Word, including:
- Headers (h1-h6)
- Text formatting (bold, italic, strikethrough)
- Links
- Lists (ordered and unordered)
- Tables
- Code blocks
- Blockquotes
- Horizontal rules
- Images

The tool must convert the complete content with equivalent formatting in Word.

## Tech Stack
- **Language**: Java
- **JDK Version**: 17.0.12
- **Build Tool**: Maven (inferred - standard for Java projects)
- **Word Generation**: Apache POI (for .docx creation)
- **Markdown Parsing**: CommonMark Java (flexmark-java or similar)
- **Testing**: JUnit 5 (standard for Java)

## Commands
- Build: `mvn clean package`
- Test: `mvn test`
- Run: `java -jar target/markdown-to-word.jar [input.md] [output.docx]`

## Technical Constraints
- Must use Java 17 (current environment: JDK 17.0.12)
- Source code must be placed in **MarkDownToWordSource** directory
- Must generate a README.md in the source directory with usage instructions
- Must include comprehensive test cases
- Output must be valid .docx format
- All content from source MarkDown must be present in output
- Formatting must be equivalent to MarkDown source

## References
- specs/API.md - Core converter API specification
- specs/MARKDOWN_MAPPING.md - Mapping of MarkDown elements to Word formats
- specs/TEST_SPEC.md - Test case specifications

## Project Structure
```
MarkDownToWordSource/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── markdown/
│   │               └── toword/
│   │                   ├── MarkdownConverter.java  # Main entry point
│   │                   ├── parser/                  # Markdown parsing
│   │                   ├── converter/               # Word conversion
│   │                   └── model/                   # Data models
│   └── test/
│       └── java/
│           └── com/
│               └── markdown/
│                   └── toword/
│                       └── MarkdownConverterTest.java
├── pom.xml
└── README.md
```
