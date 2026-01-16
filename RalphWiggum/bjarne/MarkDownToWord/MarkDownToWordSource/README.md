# MarkDownToWord

A tool to convert Markdown files to Word documents.

## Project Details

- **GroupId**: com.markdowntoword
- **ArtifactId**: MarkDownToWord
- **Version**: 1.0
- **Packaging**: jar

## Prerequisites

- JDK 17 or higher
- Maven 3.6 or higher

## Build Instructions

To build the project:

```bash
mvn clean package
```

This will create an executable JAR file in the `target/` directory:
- `MarkDownToWord-1.0.jar` (executable JAR with dependencies)

## Usage

To run the application:

```bash
java -jar target/MarkDownToWord-1.0.jar <input.md> <output.docx>
```

Example:

```bash
java -jar target/MarkDownToWord-1.0.jar input.md output.docx
```

## Dependencies

- **Apache POI 5.2.5** - Word document generation
  - `poi`: Core POI library
  - `poi-ooxml`: OOXML support for .docx format
- **Flexmark-java 0.64.8** - Markdown parsing (flexmark-all)
- **JUnit 5.10.0** - Testing framework

## Project Structure

```
MarkDownToWordSource/
├── src/
│   ├── main/java/com/markdowntoword/    # Main Java source code
│   └── test/java/com/markdowntoword/    # Test Java source code
├── testFiles/                            # Test files directory
├── target/                               # Build output directory
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## Testing

To run tests:

```bash
mvn test
```

## Features

- [x] Project structure setup
- [x] Maven configuration
- [ ] Markdown parsing
- [ ] Word document generation
- [ ] Format conversion (headings, lists, tables, etc.)
- [ ] Command-line interface

## Documentation

- [SPECIFICATION.md](../specs/SPECIFICATION.md) - Detailed feature specifications
- [CONTEXT.md](../CONTEXT.md) - Project context and overview
- [idea.md](../idea.md) - Original requirements document

## License

[Add license information]
