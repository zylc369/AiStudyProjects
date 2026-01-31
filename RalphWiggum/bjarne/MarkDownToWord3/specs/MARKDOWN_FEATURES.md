# Markdown Features Specification

This document details all Markdown features that must be supported and their corresponding Word format equivalents.

## Headers (H1-H6)

### Markdown Syntax
```markdown
# Heading 1
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6
```

### Word Format
- Use Word built-in styles: "Heading1", "Heading2", "Heading3", "Heading4", "Heading5", "Heading6"
- Preserve inline formatting within headers (bold, italic, code, etc.)

## Text Formatting

### Bold
**Markdown**: `**bold text**` or `__bold text__`
**Word**: `XWPFRun.setBold(true)`

### Italic
**Markdown**: `*italic text*` or `_italic text_`
**Word**: `XWPFRun.setItalic(true)`

### Bold + Italic
**Markdown**: `***bold italic***` or `___bold italic___`
**Word**: `XWPFRun.setBold(true)` + `XWPFRun.setItalic(true)`

### Strikethrough
**Markdown**: `~~strikethrough~~`
**Word**: `XWPFRun.setStrikeThrough(true)`

### Inline Code
**Markdown**: `` `code` ``
**Word**: Use monospace font (e.g., "Courier New") with light gray background

## Links

### Hyperlinks
**Markdown**: `[link text](https://example.com)`
**Word**: `XWPFHyperlink` with `HyperlinkType.EXTERNAL`

### Images
**Markdown**: `![alt text](image.png)`
**Word**: Insert embedded image using `XWPFRun.addPicture()`

## Lists

### Unordered Lists
**Markdown**:
```markdown
- Item 1
- Item 2
  - Nested item
```
**Word**: Use `XWPFParagraph.setNumID()` with bullet style

### Ordered Lists
**Markdown**:
```markdown
1. First item
2. Second item
```
**Word**: Use `XWPFParagraph.setNumID()` with numbered style

### Task Lists
**Markdown**:
```markdown
- [ ] Unchecked task
- [x] Checked task
```
**Word**: Use checkbox symbols or Wingdings font

## Block Elements

### Code Blocks
**Markdown** (fenced):
```markdown
```
code here
```
```
**Word**: Monospace font, light gray background, preserve whitespace

### Blockquotes
**Markdown**:
```markdown
> Quote text
> > Nested quote
```
**Word**: Indented paragraph, italic text, left border

### Horizontal Rules
**Markdown**: `---`, `***`, or `___`
**Word**: Horizontal line using paragraph border

## Tables

### Markdown Syntax
```markdown
| Header 1 | Header 2 |
|----------|----------|
| Cell 1   | Cell 2   |
```

### Word Format
- Use `XWPFTable`
- Set borders on all cells
- Handle row/column spanning
- Apply bold to header row
- Handle alignment (left, center, right)

## Implementation Notes

### Processing Order
1. Parse Markdown to AST using flexmark
2. Traverse AST in document order
3. For each node type, apply appropriate converter

### Converter Classes
- `MarkdownParser` - Parse Markdown to AST
- `MarkdownConverter` - Orchestrate conversion
- `HeaderConverter` - H1-H6
- `ParagraphConverter` - Paragraphs
- `InlineTextConverter` - Bold, italic, code, links
- `ListConverter` - Ordered and unordered lists
- `TableConverter` - Tables
- `CodeBlockConverter` - Fenced and indented code blocks
- `BlockquoteConverter` - Blockquotes
- `ImageConverter` - Images
- `HorizontalRuleConverter` - HR

### Error Handling
- `ConversionException` for conversion failures
- Validate null/empty inputs
- Handle file I/O errors gracefully
