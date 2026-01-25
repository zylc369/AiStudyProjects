# Markdown to Word Format Mapping

This document specifies how each Markdown element maps to Word formatting.

## Text Formatting

| Markdown | Word Format |
|----------|-------------|
| `# Text` | Heading 1 style (18pt, bold) |
| `## Text` | Heading 2 style (16pt, bold) |
| `### Text` | Heading 3 style (14pt, bold) |
| `#### Text` | Heading 4 style (12pt, bold) |
| `##### Text` | Heading 5 style (11pt, bold) |
| `###### Text` | Heading 6 style (10pt, bold) |
| `**bold**` | Bold run |
| `*italic*` | Italic run |
| `***bold italic***` | Bold + Italic run |
| `` `inline code` `` | Consolas font, 9pt, light gray background |
| `[text](url)` | Hyperlink with blue, underlined |

## Block Elements

| Markdown | Word Format |
|----------|-------------|
| Plain paragraph | Normal style (11pt), normal spacing |
| `> quote` | Indented, italic, left border |
| Code block (```) | Consolas font, 9pt, light gray background box |
| Horizontal rule (`---`) | Paragraph with bottom border |

## Lists

### Unordered Lists

| Markdown | Word Format |
|----------|-------------|
| `- item` | Bullet paragraph with default bullet |
| `- nested item` | Indented bullet paragraph |

### Ordered Lists

| Markdown | Word Format |
|----------|-------------|
| `1. item` | Numbered paragraph (decimal) |
| `a. item` | Numbered paragraph (lower letter) |
| `   1. nested` | Indented numbered paragraph |

## Tables

| Markdown | Word Format |
|----------|-------------|
| `| Header |` | Table row with bold text, bottom border |
| `| Cell |` | Normal table cell |
| `| --- |` | Column separator (defines alignment) |

Alignment mappings:
- `:---` : Left aligned
- `:---:` : Center aligned
- `---:` : Right aligned

## Code Blocks

 fenced (```) or indented:
```
code here
```

Maps to:
- Paragraph with Consolas 9pt font
- Light gray background (RGB 245, 245, 245)
- No spacing between lines
- Preserve all whitespace

## Links

| Markdown | Word Format |
|----------|-------------|
| `[text](url)` | XWPFHyperlink with blue, underlined text |
| `[text](url "title")` | Hyperlink with tooltip |

## Special Cases

### Line Breaks
- Single newline: Space (Word normal behavior)
- Two newlines: New paragraph
- Trailing spaces (2+): Line break (not paragraph break)

### Escape Sequences
- `\*` → Literal asterisk (no formatting)
- `\\` → Literal backslash

### HTML in Markdown
- Supported HTML tags should be rendered if possible
- Unsupported tags: Strip tags, keep content

## Style Definitions

All styles use:
- Base font: Calibri (or system default)
- Base size: 11pt
- Line spacing: 1.15
- Paragraph spacing after: 10pt

Code block style:
- Font: Consolas or Courier New
- Size: 9pt
- Background: RGB 245, 245, 245
- Preserve spacing: 0pt after
