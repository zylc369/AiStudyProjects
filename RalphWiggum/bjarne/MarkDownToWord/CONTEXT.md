# MarkDown to Word Converter

## What We're Building

A Java-based tool that converts Markdown files to Word documents (.docx) with rich text formatting. The tool must parse Markdown syntax and convert it to equivalent Word formatting including:
- Headers (h1-h6)
- Bold and italic text
- Links
- Lists (ordered and unordered)
- Tables
- Code blocks
- Blockquotes/alerts

## Existing Codebase

This is a new project. No existing codebase.

## Tech Stack

- **Language**: Java
- **JDK Version**: 17.0.12
- **Build Tool**: Maven (to be determined)
- **Core Libraries**:
  - Markdown parsing: flexmark-java or similar
  - Word generation: Apache POI (XWPF) or docx4j
  - Testing: JUnit 5

## Commands

```bash
# Build (Maven)
mvn clean package

# Run tests
mvn test

# Run the tool
java -jar target/markdown-to-word.jar <input.md> <output.docx>
```

## Project Structure

```
MarkDownToWordSource/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── md2word/
│   │               ├── MarkdownConverter.java    # Main entry point
│   │               ├── parser/                   # Markdown parsing
│   │               └── writer/                   # Word document generation
│   └── test/
│       └── java/
│           └── com/
│               └── md2word/
│                   └── MarkdownConverterTest.java
├── pom.xml                                     # Maven configuration
└── README.md                                   # User documentation
```

## Key Decisions

1. **Source directory**: All source code must be placed in `MarkDownToWordSource/` directory as specified
2. **Complete conversion**: All Markdown content must be converted - no content loss allowed
3. **Rich text format**: Output must use Word's formatting features, not plain text
4. **Testing required**: Test cases must be written to verify correctness

## References

- specs/ARCHITECTURE.md - Detailed design decisions
- specs/FORMAT_MAPPING.md - Markdown to Word format mappings
- TASKS.md - Implementation roadmap
