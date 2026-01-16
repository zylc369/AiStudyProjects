# MarkDownToWord - Implementation Tasks

## Setup

- [x] Create MarkDownToWordSource directory structure → Directory exists with src/main, src/test, testFiles subdirectories
- [ ] Create Maven pom.xml with required dependencies → pom.xml contains Apache POI and Flexmark-java dependencies
- [ ] Create base Java package structure → com.markdowntoword package with main and converter subpackages
- [ ] Create basic README.md template → README.md exists in MarkDownToWordSource with placeholder content

## Core Conversion Engine

- [ ] Create MarkdownParser class → Can parse a Markdown file and return a document AST
- [ ] Create WordDocumentBuilder class → Can create and write .docx files with basic content
- [ ] Implement heading conversion → Markdown headings (# ## ###) convert to Word paragraphs with appropriate styles
- [ ] Implement paragraph conversion → Markdown paragraphs convert to Word paragraphs
- [ ] Implement bold formatting → Markdown **text** converts to Word bold text
- [ ] Implement italic formatting → Markdown *text* converts to Word italic text
- [ ] Implement bold-italic combination → Markdown ***text*** converts to Word bold+italic text

## Advanced Formatting

- [ ] Implement link conversion → Markdown [text](url) converts to Word hyperlink
- [ ] Implement inline code conversion → Markdown `code` converts to Word inline monospace
- [ ] Implement code block conversion → Markdown ```code``` converts to Word preformatted text block
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
- [ ] Create unit tests for WordDocumentBuilder → Tests pass for document creation
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
