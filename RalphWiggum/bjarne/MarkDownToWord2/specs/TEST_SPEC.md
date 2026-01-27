# Test Specification

## Test Structure

```
src/test/resources/
├── simple.md                 # Simple paragraph
├── headings.md              # All heading levels
├── formatting.md            # Bold, italic, code
├── lists.md                 # Ordered and unordered
├── nested-lists.md          # Nested lists
├── tables.md                # Tables
├── links.md                 # Links
├── images.md                # Images
├── code-blocks.md           # Code blocks
├── blockquotes.md           # Blockquotes
├── complex.md               # All features combined
└── chinese.md               # Chinese content test
```

## Test Cases

### 1. Heading Conversion Test
**File**: `headings.md`
```markdown
# Heading 1
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6
```

**Verification**:
- Word document has 6 paragraphs
- Each paragraph uses correct heading style (Heading 1-6)
- Font sizes are 16pt, 14pt, 13pt, 12pt, 11pt, 10pt
- All text is bold

### 2. Text Formatting Test
**File**: `formatting.md`
```markdown
This is **bold text**.
This is *italic text*.
This is ***bold and italic***.
This is `inline code`.
This is ~~strikethrough~~.
```

**Verification**:
- "bold text" has isBold = true
- "italic text" has isItalic = true
- "bold and italic" has both properties
- "inline code" uses Consolas font
- "strikethrough" has strikeThrough = true

### 3. List Test
**File**: `lists.md`
```markdown
Unordered:
- Item 1
- Item 2
- Item 3

Ordered:
1. First
2. Second
3. Third
```

**Verification**:
- Unordered list uses bullet numbering
- Ordered list uses decimal numbering
- Each list item is a separate paragraph
- Lists are separated by paragraph

### 4. Nested List Test
**File**: `nested-lists.md`
```markdown
- Level 1
  - Level 2
    - Level 3
- Another L1
  - Another L2
```

**Verification**:
- Three indentation levels detected
- Level 2 has indent > Level 1
- Level 3 has indent > Level 2
- Different bullet styles for each level

### 5. Table Test
**File**: `tables.md`
```markdown
| Name | Age | City |
|------|-----|------|
| Alice | 25 | NYC |
| Bob | 30 | LA |
```

**Verification**:
- Word document contains 1 table
- Table has 2 rows (header + 1 data)
- Table has 3 columns
- Header row has bold text
- All cells have borders

### 6. Link Test
**File**: `links.md`
```markdown
[OpenAI](https://openai.com)
<https://github.com>
```

**Verification**:
- First hyperlink: text="OpenAI", URI="https://openai.com"
- Second hyperlink: text="https://github.com", URI="https://github.com"
- Hyperlinks are clickable

### 7. Code Block Test
**File**: `code-blocks.md`
````markdown
```
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```
````
**Verification**:
- Code block has monospace font
- Background color is #F5F5F5
- Border exists around code
- Whitespace is preserved

### 8. Blockquote Test
**File**: `blockquotes.md`
```markdown
> This is a quote
> That spans multiple lines
```

**Verification**:
- Text is italic
- Left border exists
- Left indent is 20pt
- Color is gray

### 9. Complex Document Test
**File**: `complex.md`
Contains all features combined.

**Verification**:
- All content from Markdown appears in Word
- All formatting is correct
- No content is lost
- Document structure is preserved

### 10. Chinese Content Test
**File**: `chinese.md`
```markdown
# 中文标题
这是一段**粗体**文字。
这是一个*斜体*句子。
```

**Verification**:
- Chinese characters display correctly
- Formatting applies to Chinese text
- No encoding issues

## Test Implementation

### Unit Tests

```java
@Test
public void testHeadingsConversion() throws IOException {
    String input = "src/test/resources/headings.md";
    String output = "target/test-output/headings.docx";

    converter.convert(input, output);

    // Verify output file exists
    assertTrue(Files.exists(Paths.get(output)));

    // Verify heading styles
    XWPFDocument doc = new XWPFDocument(new FileInputStream(output));
    // ... assertions
}

@Test
public void testBoldFormatting() throws IOException {
    String markdown = "This is **bold** text.";
    // Convert and verify bold run
}
```

### Integration Test

```java
@Test
public void testComplexDocument() throws IOException {
    String input = "src/test/resources/complex.md";
    String output = "target/test-output/complex.docx";

    converter.convert(input, output);

    // Verify file created
    assertTrue(Files.exists(Paths.get(output)));

    // Verify content preservation
    XWPFDocument doc = new XWPFDocument(new FileInputStream(output));
    // Count elements and compare with source
}
```

## Automated Verification Criteria

### Success Criteria
1. All unit tests pass (100%)
2. Integration test passes
3. No exceptions thrown during conversion
4. Output file is valid .docx format
5. Output file can be opened in Microsoft Word

### Content Verification
For each test:
1. Count of paragraphs matches or exceeds Markdown
2. All text from Markdown appears in output
3. Styles are applied correctly
4. Hyperlinks are valid
5. Tables have correct dimensions
6. Lists have correct numbering

### Maven Commands
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=HeadingConversionTest

# Generate test report
mvn surefire-report:report
```
