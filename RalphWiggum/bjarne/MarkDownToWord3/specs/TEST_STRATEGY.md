# Test Strategy

## Testing Approach

### Unit Tests
Test each converter class in isolation with focused test cases.

### Integration Tests
Test the full conversion pipeline from Markdown string/file to Word document.

### Verification Tests
Confirm that the generated Word documents contain expected content and formatting.

## Test Coverage Requirements

### MarkdownParserTest
- Valid Markdown input produces non-null Document
- Null input throws IllegalArgumentException
- Empty string produces valid Document

### HeaderConverterTest
- H1-H6 headers create correct Word heading styles
- Headers with inline formatting (bold, italic, code) preserve formatting
- Multiple headers in document all converted

### ParagraphConverterTest
- Plain paragraphs convert to Word paragraphs
- Paragraphs with bold text
- Paragraphs with italic text
- Paragraphs with mixed formatting
- Empty lines create blank paragraphs

### InlineTextConverterTest
- Bold text (`**bold**` and `__bold__`)
- Italic text (`*italic*` and `_italic_`)
- Bold + italic (`***bolditalic***`)
- Strikethrough (`~~strikethrough~~`)
- Inline code (`` `code` ``)
- Plain text
- Mixed formatting in single paragraph

### ListConverterTest
- Unordered lists with bullets
- Ordered lists with numbers
- Nested lists (2-3 levels)
- Lists with formatting (bold, italic)
- Empty list items

### TableConverterTest
- Simple table (2 columns, 2 rows)
- Table with headers
- Table with inline formatting in cells
- Table alignment (left, center, right)
- Multi-row and multi-column spanning

### CodeBlockConverterTest
- Fenced code blocks (```)
- Tilde code blocks (~~~)
- Code blocks with syntax highlighting indicator
- Empty code blocks
- Code blocks preserving whitespace

### BlockquoteConverterTest
- Single-line blockquotes
- Multi-line blockquotes
- Nested blockquotes
- Blockquotes with inline formatting

### ImageConverterTest
- Local image files
- Images with alt text
- Images with titles

### MarkdownConverterTest (Integration)
- Complete Markdown document with all features
- Null input throws ConversionException
- Empty output path throws ConversionException
- File input/output

## Test Resources

Create test Markdown files in `src/test/resources/`:

```
src/test/resources/
├── simple.md                    # Basic headers and paragraphs
├── formatting.md                # Bold, italic, strikethrough, code
├── lists.md                     # Ordered and unordered lists
├── tables.md                    # Various table formats
├── code.md                      # Code blocks and inline code
├── blockquotes.md               # Blockquote variations
├── complete.md                  # All features combined
└── images/
    └── test-image.png           # Sample image for testing
```

## Verification Approach

### Content Verification
For each test, verify that the Word document contains:
- All text from the original Markdown
- No missing content

### Formatting Verification
Verify formatting by checking:
- Bold: `XWPFRun.isBold()`
- Italic: `XWPFRun.isItalic()`
- Heading styles: `XWPFParagraph.getStyle()`
- List numbering: `XWPFParagraph.getNumID()`
- Font families for code blocks

### Test Execution

All tests must pass:
```bash
mvn test
```

Expected output: All tests pass with no failures.

## Acceptance Criteria

1. **Compilation**: `mvn clean package` succeeds with exit code 0
2. **Tests**: All unit and integration tests pass
3. **Content**: No text content is lost during conversion
4. **Formatting**: All formatting is equivalent to Markdown source
