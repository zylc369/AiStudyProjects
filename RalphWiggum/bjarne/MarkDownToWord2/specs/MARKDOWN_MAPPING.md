# Markdown to Word Format Mapping

This document details how each Markdown element maps to Word formatting.

## Text Formatting

### Headings

| Markdown | Word Style | Font Size | Font Weight |
|----------|------------|-----------|-------------|
| `# Heading 1` | Heading 1 | 16pt | Bold |
| `## Heading 2` | Heading 2 | 14pt | Bold |
| `### Heading 3` | Heading 3 | 13pt | Bold |
| `#### Heading 4` | Heading 4 | 12pt | Bold |
| `##### Heading 5` | Heading 5 | 11pt | Bold |
| `###### Heading 6` | Heading 6 | 10pt | Bold |

### Character Styles

| Markdown | Word Format | Implementation |
|----------|-------------|----------------|
| `**bold text**` | Bold | `XWPFRun.setBold(true)` |
| `*italic text*` | Italic | `XWPFRun.setItalic(true)` |
| `***bold italic***` | Bold + Italic | Both properties set |
| `~~strikethrough~~` | Strikethrough | `XWPFRun.setStrikeThrough(true)` |
| `` `inline code` `` | Monospace | Font: Consolas, Size: 10pt |

## Structural Elements

### Paragraphs
- Markdown: Empty line separation
- Word: New paragraph (`XWPFParagraph`)
- Spacing: After = 10pt

### Horizontal Rules
- Markdown: `---`, `***`, or `___`
- Word: Horizontal line using paragraph border

## Lists

### Unordered Lists
- Markdown: `-`, `*`, or `+`
- Word: Numbering definition with bullet style
- Nesting: Indent level 1-9

### Ordered Lists
- Markdown: `1.`, `2.`, `3.`
- Word: Numbering definition with decimal style
- Nesting: Indent level 1-9, different numbering schemes per level

### List Nesting Rules
```
Level 1: 1. / 1) / - / *
Level 2: a. / a) / - (dash)
Level 3: i. / i) / • (circle)
Level 4+: Continue pattern
```

## Code Blocks

### Inline Code
```
Markdown: `code here`
Word: Font "Consolas", Size 10pt, Color #D83B01 (orange-ish)
```

### Fenced Code Blocks
```
Markdown:
```
code block
```
```
Word:
- Paragraph style "CodeBlock"
- Font: Consolas, 9pt
- Background: #F5F5F5 (light gray)
- Border: 1pt solid #CCCCCC
- Spacing: Before/After = 6pt
```

## Links

### Hyperlinks
```
Markdown: [link text](https://example.com)
Word: XWPFHyperlink with URI set
```

### Automatic Links
```
Markdown: <https://example.com>
Word: XWPFHyperlink with URL as text and URI
```

## Images

### Inline Images
```
Markdown: ![alt text](path/to/image.png)
Word: XWPFPicture embedded in paragraph
```

## Blockquotes

### Quotes
```
Markdown: > quoted text
Word:
- Font: Italic
- Left border: 3pt, color #4472C4
- Indent: Left 20pt
- Color: #595959 (dark gray)
```

## Tables

### Table Structure
```
Markdown:
| Header 1 | Header 2 |
|----------|----------|
| Cell 1   | Cell 2   |
```
Word:
- `XWPFTable` with border
- Header row: Bold text, background #D9D9D9
- Cell borders: 1pt single line
- Auto-fit: Window resize
- Alignment: As specified in Markdown (default left)

## Special Characters

### HTML Entities
| Entity | Word |
|--------|------|
| `&amp;` | & |
| `&lt;` | < |
| `&gt;` | > |
| `&quot;` | " |

### Escaped Characters
| Escaped | Word |
|---------|------|
| `\*` | * |
| `\` | \ |
| `\[` | [ |

## Whitespace Handling

### Line Breaks
- Markdown: Single newline → Word: Space (soft wrap)
- Markdown: Two spaces + newline → Word: Line break (forced)
- Markdown: Blank line → Word: New paragraph

### Multiple Spaces
- Markdown: Multiple spaces → Word: Single space (collapsed)
- Code blocks: Preserve all spaces
