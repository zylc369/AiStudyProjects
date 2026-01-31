# Testing Specification

## Test Coverage Requirements

### 1. Unit Tests

#### 1.1 MarkdownParser Tests
```java
class MarkdownParserTest {
    @Test
    void parseSimpleHeading() { }
    @Test
    void parseParagraph() { }
    @Test
    void parseBoldText() { }
    @Test
    void parseItalicText() { }
    @Test
    void parseLink() { }
    @Test
    void parseUnorderedList() { }
    @Test
    void parseOrderedList() { }
    @Test
    void parseTable() { }
    @Test
    void parseCodeBlock() { }
    @Test
    void parseBlockquote() { }
}
```

#### 1.2 WordGenerator Tests
```java
class WordGeneratorTest {
    @Test
    void generateEmptyDocument() { }
    @Test
    void generateDocumentWithHeadings() { }
    @Test
    void generateDocumentWithFormattedText() { }
    @Test
    void generateDocumentWithLists() { }
    @Test
    void generateDocumentWithTables() { }
}
```

### 2. Integration Tests

#### 2.1 End-to-End Conversion Test
```java
class MarkdownToWordConverterIntegrationTest {
    @Test
    void convertCompleteMarkdownDocument() {
        // Arrange
        Path input = createTestMarkdown();
        Path output = tempDir.resolve("output.docx");

        // Act
        converter.convert(input, output);

        // Assert
        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0);
        verifyContent(output);
    }
}
```

### 3. Test Data

#### 3.1 Sample Markdown Files

**simple.md**
```markdown
# Heading 1
This is a paragraph.

## Heading 2
- List item 1
- List item 2
```

**formatting.md**
```markdown
# Text Formatting

This is **bold text** and this is *italic text*.
This is ***bold italic*** text.

A [link](https://example.com) example.
```

**lists.md**
```markdown
# Lists

Unordered:
- Item 1
- Item 2
  - Nested item
  - Another nested
- Item 3

Ordered:
1. First
2. Second
3. Third
```

**tables.md**
```markdown
# Tables

| Header 1 | Header 2 | Header 3 |
|----------|----------|----------|
| Cell 1   | Cell 2   | Cell 3   |
| Cell 4   | Cell 5   | Cell 6   |
```

**code.md**
```markdown
# Code Examples

Inline `code` example.

Block code:
```
public void example() {
    System.out.println("Hello");
}
```
```

**complete.md** - Contains all features combined

### 4. Verification Methods

#### 4.1 Automated Verification
```java
// Verify file exists and is non-empty
assertTrue(Files.exists(outputPath));
assertTrue(Files.size(outputPath) > 0);

// Verify document structure
// (Requires parsing the .docx back or using inspection library)
```

#### 4.2 Manual Verification Checklist
For each test run, manually verify:
- [ ] Document opens in Word without errors
- [ ] All text from Markdown is present
- [ ] Headings have correct hierarchy
- [ ] Bold/italic text is formatted
- [ ] Lists maintain structure
- [ ] Tables have borders
- [ ] Code uses monospace font
- [ ] Links are clickable

### 5. Test Execution

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MarkdownParserTest

# Run with coverage
mvn test jacoco:report
```

### 6. Expected Test Results
- All tests pass: 0 failures, 0 errors
- Test coverage > 80% for core conversion logic
- Integration test produces valid .docx file
