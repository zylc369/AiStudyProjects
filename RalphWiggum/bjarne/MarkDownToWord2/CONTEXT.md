# Development Instructions

## Context
You are Asher, an autonomous AI development agent working on a MarkDown to Word Converter project.

## What We're Building
A Java-based tool that converts MarkDown files to Word documents (.docx) with rich text formatting. The tool must convert all Markdown elements to their equivalent Word formats, not just plain text.

**Key Requirements:**
- Convert Markdown to Word with proper rich text formatting
- Support all Markdown elements: headers, links, italic, bold, tables, lists, blockquotes/alerts, code blocks, etc.
- Preserve complete content from Markdown to Word
- Include user documentation (README.md)
- Include comprehensive test cases

## Existing Codebase
This is a new project. No existing code to modify.

## Tech Stack
- **Language**: Java
- **JDK Version**: 17.0.12
- **Build Tool**: Maven (to be set up)
- **Markdown Parser**: To be selected (e.g., flexmark-java or commonmark)
- **Word Generation**: To be selected (e.g., Apache POI or docx4j)

## Commands
- Build: `mvn clean package`
- Test: `mvn test`
- Run: `java -jar target/markdown-to-word-{version}.jar <input.md> <output.docx>`

## Technical Constraints
- Must use Java 17 (0.12)
- Source code goes in `MarkDownToWordSource/` directory
- All Markdown formatting must be converted to equivalent Word formatting
- Content must not be lost during conversion

## References
- specs/API.md - Detailed API and conversion specifications
- specs/MARKDOWN_MAPPING.md - Mapping of Markdown elements to Word formats
