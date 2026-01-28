# Test Specification

## Test Infrastructure

### Test Framework
- **JUnit 5** (Jupiter) for test execution
- **AssertJ** for fluent assertions
- **JUnit Pioneer** for temporary file handling

### Test Base Class
```java
abstract class MarkdownConverterTestBase {
    protected MarkdownConverter converter;
    protected Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        converter = new MarkdownConverter();
        tempDir = Files.createTempDirectory("md-test-");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp files
    }
}
```

## Unit Tests

### Header Conversion Tests

#### Test: ConvertH1Header
**Input:**
```markdown
# Main Title
```
**Expected:**
- Output document has 1 paragraph
- Paragraph style is "Heading1"
- Text content is "Main Title"

#### Test: ConvertAllHeaderLevels
**Input:**
```markdown
# H1
## H2
### H3
#### H4
##### H5
###### H6
```
**Expected:**
- 6 paragraphs created
- Each has correct heading style (Heading1 through Heading6)

### Text Formatting Tests

#### Test: ConvertBoldText
**Input:**
```markdown
This is **bold** text.
```
**Expected:**
- 1 paragraph with 3 runs
- Run 1: "This is " (plain)
- Run 2: "bold" (bold=true)
- Run 3: " text." (plain)

#### Test: ConvertItalicText
**Input:**
```markdown
This is *italic* text.
```
**Expected:**
- Run 2 has italic=true

#### Test: ConvertBoldAndItalic
**Input:**
```markdown
This is ***bolditalic*** text.
```
**Expected:**
- Run 2 has both bold=true and italic=true

#### Test: ConvertStrikethrough
**Input:**
```markdown
This is ~~deleted~~ text.
```
**Expected:**
- Run 2 has strikeThrough=true

#### Test: ConvertInlineCode
**Input:**
```markdown
Use `System.out.println()` for output.
```
**Expected:**
- Run 2 uses "Courier New" font family
- Run 2 font size is 10pt

### List Conversion Tests

#### Test: ConvertUnorderedList
**Input:**
```markdown
- Apple
- Banana
- Cherry
```
**Expected:**
- 3 paragraphs with bullet numbering
- Each paragraph indent level = 0

#### Test: ConvertNestedUnorderedList
**Input:**
```markdown
- Fruit
  - Apple
  - Banana
- Vegetable
```
**Expected:**
- 4 paragraphs total
- "Apple" and "Banana" have indent level = 1

#### Test: ConvertOrderedList
**Input:**
```markdown
1. First
2. Second
3. Third
```
**Expected:**
- 3 paragraphs with decimal numbering
- Numbering format is NUM_FMT.DECIMAL

### Code Block Tests

#### Test: ConvertFencedCodeBlock
**Input:**
````markdown
```
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```
````
**Expected:**
- 1 paragraph created
- Font is "Courier New"
- Font size is 9pt
- Background color is RGB(245, 245, 245)

#### Test: PreserveCodeWhitespace
**Input:**
````markdown
```
Line1
  Line2  indented
```
````
**Expected:**
- Both leading and trailing spaces preserved in output

### Table Conversion Tests

#### Test: ConvertSimpleTable
**Input:**
```markdown
| Name | Age |
|------|-----|
| John | 30  |
```
**Expected:**
- Document contains 1 XWPFTable
- Table has 2 rows and 2 columns
- Header row has bold text
- Header row has gray background (RGB 217, 217, 217)

#### Test: ConvertTableWithAlignment
**Input:**
```markdown
| Left | Center | Right |
|:-----|:------:|------:|
| A | B | C |
```
**Expected:**
- Column 0: left aligned
- Column 1: center aligned
- Column 2: right aligned

### Link Conversion Tests

#### Test: ConvertBasicLink
**Input:**
```markdown
[OpenAI](https://openai.com)
```
**Expected:**
- 1 hyperlink run created
- Display text is "OpenAI"
- Hyperlink URL is "https://openai.com"

### Image Conversion Tests

#### Test: ConvertImageWithAbsolutePath
**Input:**
```markdown
![Alt text](/path/to/image.png)
```
**Setup:**
- Create test image at specified path

**Expected:**
- 1 picture run created
- Picture description (alt text) is "Alt text"

#### Test: ConvertImageWithRelativePath
**Input:**
```markdown
![Alt text](./images/test.png)
```
**Setup:**
- Convert file at `/tmp/test/input.md`
- Create image at `/tmp/test/images/test.png`

**Expected:**
- Image resolved relative to markdown file location
- Picture embedded successfully

### Blockquote Tests

#### Test: ConvertBlockquote
**Input:**
```markdown
> This is a quote.
```
**Expected:**
- 1 paragraph created
- Left indentation = 720 twips (0.5 inch)
- Text is italic

### Horizontal Rule Tests

#### Test: ConvertHorizontalRule
**Input:**
```markdown
Text above
---
Text below
```
**Expected:**
- 3 paragraphs
- Middle paragraph has bottom border

## Integration Tests

### Test: ConvertComplexDocument
**Input:**
```markdown
# Sample Document

This is a **test document** with *various* formatting.

## Features

- Bullet points
- Numbered lists
  - Nested items

1. First item
2. Second item

```java
public class Test {
    // Code here
}
```

| Column 1 | Column 2 |
|----------|----------|
| Data 1   | Data 2   |

> A blockquote here

[A link](https://example.com)

---

End of document.
```

**Expected:**
- All elements converted without errors
- Output .docx file is valid and can be opened
- Content count matches input (verify no content loss)

## Verification Tests

### Test: ContentCompleteness
**Purpose:** Verify no content is lost during conversion

**Method:**
1. Convert test Markdown
2. Extract all text from output .docx
3. Extract all text from input Markdown (excluding markup)
4. Assert: Output text contains all input text (case-insensitive)

### Test: OutputFileValidity
**Purpose:** Verify output .docx can be opened

**Method:**
1. Convert test Markdown
2. Open output with Apache POI (XWPFDocument)
3. Assert: No exceptions thrown
4. Assert: Document has at least 1 paragraph

## Test Data

### Test Files Location
- `src/test/resources/markdown/` - Input .md files
- `src/test/resources/images/` - Test images
- `src/test/resources/expected/` - Expected output metadata

### Test Markdown Files
- `simple.md` - Basic formatting
- `headers.md` - All header levels
- `lists.md` - Various list types
- `tables.md` - Table examples
- `code.md` - Code blocks
- `links.md` - Links and images
- `complex.md` - Combined elements
- `edge-cases.md` - Unusual scenarios

## Test Execution

### Maven Commands
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MarkdownConverterTest

# Run specific test method
mvn test -Dtest=MarkdownConverterTest#testConvertBoldText
```

### Test Coverage Goal
- Minimum line coverage: 80%
- Branch coverage: 70%
- All public API methods tested
- All markdown elements covered
