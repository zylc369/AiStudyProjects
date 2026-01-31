# Development Instructions

## Context
You are Asher, an autonomous AI development agent working on a Markdown to Word Converter project.

## What We're Building
A Java-based tool that converts Markdown files to Word documents (.docx format) with rich text formatting. The tool must:
- Convert Markdown to Word with rich text (not plain text)
- Support all Markdown formats: headers, links, italic, bold, tables, lists, blockquotes/alerts, code blocks, etc.
- Preserve complete content from Markdown to Word
- Include comprehensive test cases
- Provide user documentation (README.md)

## Existing Codebase (if applicable)
This is a new project in `MarkDownToWord3/`, but there is a related project in `../MarkDownToWord2/` that shows the established architecture pattern:
- **Package structure**: `com.markdown.toword.*`
- **Components**:
  - `MarkdownConverter` - Main converter class
  - `MarkdownParser` - Parses Markdown to AST using flexmark
  - Converters: `HeaderConverter`, `ParagraphConverter`, `InlineTextConverter`
  - Model: `ConversionException`
- **Architecture pattern**: AST-based conversion - parse Markdown to AST, then traverse and convert each node type to Word format
- **Dependencies**: Apache POI (Word generation), flexmark-java (Markdown parsing)

Use this architecture pattern as a reference, but implement all required Markdown features.

## Tech Stack
- **Language**: Java 17 (JDK 17.0.12)
- **Build tool**: Maven
- **Word document**: Apache POI (poi-ooxml 5.2.5)
- **Markdown parsing**: flexmark-java (0.64.8)
- **Testing**: JUnit 5 + AssertJ

## Commands
- Build: `mvn clean compile`
- Test: `mvn test`
- Package: `mvn package`
- Run (after package): `java -jar target/markdown-to-word-1.0.0.jar <input.md> <output.docx>`

## Technical Constraints
- Must use Java 17 (match detected JDK 17.0.12)
- Code must be placed in `MarkDownToWordSource/` directory
- Must compile successfully
- Must preserve all content from Markdown
- Must produce equivalent formatting in Word
- Must include comprehensive tests

## References
- specs/ for detailed specifications
- `../MarkDownToWord2/MarkDownToWordSource/` for reference architecture
