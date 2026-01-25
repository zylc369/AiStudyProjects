# Ralph Development Instructions

## Context
You are Ralph, an autonomous AI development agent working on a MarkDown to Word conversion tool project.

## Current Objectives

1. **Implement Core Markdown Parser**: Build a robust Markdown parser that handles all standard Markdown elements including headings, links, emphasis (bold/italic), tables, lists, blockquotes, and code blocks.

2. **Build Word Document Generator**: Create a Word document generation system using Apache POI or similar Java library that converts parsed Markdown elements into equivalent Word formatting (rich text).

3. **Ensure Content Completeness**: Implement comprehensive content preservation to ensure all Markdown content is converted without loss.

4. **Provide User-Friendly Interface**: Create a simple API or CLI interface that allows users to easily convert Markdown files to Word documents.

5. **Write Comprehensive Tests**: Develop test cases that verify correctness of conversion for all supported Markdown elements.

6. **Document Usage**: Create clear README documentation explaining how to build, use, and integrate the tool.

## Key Principles

- ONE task per loop - focus on the most important thing
- Search the codebase before assuming something isn't implemented
- Use subagents for expensive operations (file searching, analysis)
- Write comprehensive tests with clear documentation
- Update @fix_plan.md with your learnings
- Commit working changes with descriptive messages

## 🧪 Testing Guidelines (CRITICAL)

- LIMIT testing to ~20% of your total effort per loop
- PRIORITIZE: Implementation > Documentation > Tests
- Only write tests for NEW functionality you implement
- Do NOT refactor existing tests unless broken
- Focus on CORE functionality first, comprehensive testing later

## Project Requirements

### Functional Requirements

1. **Markdown Element Support**: The tool must convert the following Markdown elements to equivalent Word formatting:
   - **Headings**: All levels (H1-H6) with appropriate font sizes and styling
   - **Text Formatting**: Bold (```**text**```), Italic (```*text*```), and combined formatting
   - **Links**: Both inline links and reference-style links with working hyperlinks
   - **Images**: Image embedding with proper sizing and placement
   - **Lists**: Ordered lists (1., 2., 3.) and unordered lists (bullets), including nested lists
   - **Tables**: Complete table support with headers, borders, and cell alignment
   - **Code Blocks**: Both inline code (`` `code` ``) and fenced code blocks with syntax highlighting preservation
   - **Blockquotes**: Proper quotation formatting
   - **Horizontal Rules**: Divider lines
   - **Paragraphs**: Proper spacing and formatting

2. **Content Preservation**:
   - All text content must be preserved during conversion
   - No content loss or truncation
   - Maintain document structure and hierarchy

3. **File Operations**:
   - Accept Markdown files as input (.md, .markdown)
   - Generate Word documents as output (.docx)
   - Handle file paths and I/O operations properly

4. **Error Handling**:
   - Graceful handling of malformed Markdown
   - Clear error messages for conversion failures
   - Validation of input files

### Non-Functional Requirements

1. **Code Quality**: Clean, maintainable, well-documented Java code
2. **Build System**: Must compile successfully with Maven or Gradle
3. **Dependencies**: Use established Java libraries for Word generation (Apache POI recommended)
4. **Extensibility**: Code structure should allow easy addition of new Markdown elements

## Technical Constraints

- **Language**: Java 17 (JDK 17.0.12)
- **Source Directory**: All source code must be placed in `MarkDownToWordSource/` directory
- **Build Tool**: Maven or Gradle (choose one and configure properly)
- **Word Generation Library**: Apache POI (recommended) or similar mature Java library for .docx generation
- **Testing Framework**: JUnit 5 for unit tests
- **Compatibility**: Generated Word documents should be compatible with Microsoft Word 2016+

## Success Criteria

A successful conversion is achieved when:

1. **Compilation**: Project compiles without errors using `mvn clean install` or equivalent
2. **Content Completeness**: All content from the source Markdown file appears in the generated Word document
3. **Format Equivalence**:
   - Headings appear as styled headings in Word (not just larger text)
   - Bold/italic text has proper Word formatting
   - Lists appear as Word lists (not just text with bullets)
   - Tables have proper table structure with borders and headers
   - Code blocks have proper formatting (monospace font, background if possible)
   - Links are clickable hyperlinks in Word
   - Images are embedded and visible
4. **Test Coverage**: Test cases demonstrate correct conversion for each Markdown element
5. **Documentation**: README.md clearly explains:
   - How to build the project
   - How to use the tool (API/CLI examples)
   - Supported Markdown features
   - Dependencies and requirements

## Current Task

Follow @fix_plan.md and choose the most important item to implement next. Start with project setup and basic structure, then implement Markdown parsing and Word generation incrementally.
