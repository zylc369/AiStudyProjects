# Markdown to Word Format Mapping

## Header Elements

| Markdown | Word Style | Notes |
|----------|------------|-------|
| `# H1` | Heading 1 | POI: setStyle("Heading1") |
| `## H2` | Heading 2 | POI: setStyle("Heading2") |
| `### H3` | Heading 3 | POI: setStyle("Heading3") |
| `#### H4` | Heading 4 | POI: setStyle("Heading4") |
| `##### H5` | Heading 5 | POI: setStyle("Heading5") |
| `###### H6` | Heading 6 | POI: setStyle("Heading6") |

## Text Formatting

| Markdown | Word Formatting | POI Implementation |
|----------|-----------------|-------------------|
| `**bold**` | Bold text | `setBold(true)` |
| `*italic*` | Italic text | `setItalic(true)` |
| `***bolditalic***` | Bold + Italic | Both properties set |
| `~~strikethrough~~` | Strikethrough | `setStrikeThrough(true)` |
| `` `inline code` `` | Monospace font | Font: Courier New, Size: 10 |

## Structural Elements

### Paragraphs
- Empty line separates paragraphs
- Each paragraph becomes a `XWPFParagraph`

### Horizontal Rules
- Markdown: `---` or `***`
- Word: Bottom border on paragraph
- POI: `paragraph.setBorderBottom(Borders.SINGLE)`

### Blockquotes
- Markdown: `> quoted text`
- Word: Left indent + specific style
- POI: `setIndentationLeft(720)`, italic formatting

## Lists

### Unordered Lists
| Markdown | Word Equivalent |
|----------|-----------------|
| `- item` | Bullet character • |
| `* item` | Bullet character • |
| `+ item` | Bullet character • |

- POI: Use `XWPFNumbering` with `NUM_FMT.BULLET`

### Ordered Lists
| Markdown | Word Equivalent |
|----------|-----------------|
| `1. item` | Decimal numbering 1, 2, 3... |
| `a. item` | Lowercase letter a, b, c... |
| `A. item` | Uppercase letter A, B, C... |

- Nested lists: Increase indentation level

## Code Blocks

### Fenced Code Blocks
```
```
code here
```
```

- Word: Paragraph with shaded background
- Font: Courier New (monospace)
- Size: 9pt
- Background: Light gray (RGB: 245, 245, 245)
- No spelling/grammar checking

### Indented Code Blocks
- 4 spaces or 1 tab indentation
- Same formatting as fenced code blocks

## Tables

| Markdown Element | Word Implementation |
|------------------|---------------------|
| `\| Header \|` | First row with bold text, gray background |
| `| --- |` | Column boundary markers |
| `\| Cell \|` | Regular cell |
| Alignment `|:---|` | Cell alignment (left/center/right) |

- POI: `XWPFTable` with `XWPFTableRow` and `XWPFTableCell`
- Header row: Bold text, background color (RGB: 217, 217, 217)

## Links

| Markdown | Word Implementation |
|----------|---------------------|
| `[text](url)` | Hyperlink with URL |
| `<url>` | Plain text URL |

- POI: `XWPFHyperlinkRun` with `Hyperlink` object
- Link text is the display text
- URL is stored in hyperlink target

## Images

| Markdown | Word Implementation |
|----------|---------------------|
| `![alt](path)` | Embedded inline image |

- Supported formats: PNG, JPG, JPEG, GIF
- POI: `XWPFRun.addPicture()`
- Alt text stored in picture description
- Relative path resolution from Markdown file location

## Escaping

| Markdown | Word Output |
|----------|-------------|
| `\*not italic\*` | *not italic* (plain text) |
| `\\n` | Newline character |

## Whitespace Handling

- Single newline: Space in Word (soft break)
- Double newline: New paragraph (hard break)
- Trailing spaces: Preserved
- HTML entities: Decoded (&amp; → &)

## Special Cases

### HTML in Markdown
- `<br>` → Line break in Word
- `<strong>` → Bold
- `<em>` → Italic
- Other tags: Strip or preserve as plain text based on configuration

### Task Lists
- `- [x] completed` → Paragraph with checkbox symbol (☑ or ☐)
- `- [ ] incomplete` → Paragraph with unchecked box

### Definition Lists
- `Term\n: Definition` → Bold term line + indented definition
