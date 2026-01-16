# MarkDownToWord - Implementation Tasks

## Setup

- [x] Create MarkDownToWordSource directory structure → Directory exists with src/main, src/test, testFiles subdirectories
- [x] Create Maven pom.xml with required dependencies → pom.xml contains Apache POI and Flexmark-java dependencies
- [x] Create base Java package structure → com.markdowntoword package with main and converter subpackages
- [x] Create basic README.md template → README.md exists in MarkDownToWordSource with placeholder content

## Core Conversion Engine

- [x] Create MarkdownParser class → Note: Flexmark parser doesn't need NodeExtensionList for default behavior
- [x] Create WordDocumentBuilder class → Can create and write .docx files with basic content
- [x] Implement heading conversion → Note: Used Flexmark's NodeVisitor pattern for AST traversal
- [x] Implement paragraph conversion → Note: Flexmark treats consecutive lines as single paragraph; blank lines separate paragraphs
- [x] Implement bold formatting → Note: Flexmark AST structure has StrongEmphasis as child of Paragraph
- [x] Implement italic formatting → Note: Flexmark AST structure has Emphasis as child of Paragraph
- [x] Implement bold-italic combination → Note: Add BoldItalicText method for direct bold+italic formatting

## Advanced Formatting

- [x] Implement link conversion → Markdown [text](url) converts to Word hyperlink
- [x] Implement inline code conversion → Note: Used Courier New font for monospace display
- [x] Implement code block conversion → Note: Uses Courier New font with spacing for visual separation
- [ ] Implement unordered list conversion → Markdown - item converts to Word bullet list
- [ ] Implement ordered list conversion → Markdown 1. item converts to Word numbered list
- [ ] Implement nested list conversion → Markdown nested lists convert to Word nested lists
- [ ] Implement table conversion → Markdown | col1 | col2 | converts to Word table

## Main Application

- [ ] Create Main class with CLI interface → Can run with `java -jar <input.md> <output.docx>` arguments
- [ ] Add input file validation → Validates input file exists and is readable
- [ ] Add output file handling → Creates output directory if needed, handles file overwrites
- [ ] Add progress/status output → Prints conversion progress to console

## Test Suite

- [ ] Create test Markdown files in testFiles directory → test.md, test-formats.md, test-tables.md files exist
- [ ] Create unit tests for MarkdownParser → Tests pass for parsing various Markdown elements
- [x] Create unit tests for WordDocumentBuilder → Tests pass for document creation
- [ ] Create integration tests for full conversion → Tests pass converting sample Markdown to Word
- [ ] Add content verification test → Validates converted Word contains all original content
- [ ] Add format verification test → Validates converted Word has equivalent formatting

## Documentation & Finalization

- [ ] Complete README.md with usage instructions → README.md contains clear usage examples
- [ ] Add build instructions to README → README.md contains build and run commands
- [ ] Verify all test files in testFiles directory → No test artifacts outside testFiles directory
- [ ] Run complete test suite → All tests pass with 100% content and format coverage
- [ ] Build final JAR artifact → JAR file exists in target directory with all dependencies
- [ ] Print tool path to console → Output shows full path to built JAR file
