# Edge Cases Test

This file tests edge cases and special scenarios.

## Empty Headings

#
##
###
####

## Special Characters

### HTML Entities

&copy; 2024 Test Document
&amp; symbols
&lt;tag&gt; and &quot;quotes&quot;

### Unicode Characters

Chinese: 你好世界
Japanese: こんにちは
Korean: 안녕하세요
Emoji: 😀 🎉 ❤️ 👍
Math: ∑ ∫ √ ∞ ≈ ≠ ≤ ≥

### Punctuation

...ellipsis...
---dashes---
___underscores___
**extra** *asterisks*

## Long Text

### Very Long Line

This is an extremely long line of text that should wrap properly in the converted Word document without causing any formatting issues or breaking the layout in any way shape or form regardless of how long it gets and continues to go on and on and on with more text added to make it even longer than before to really test the limits of the converter.

### Many Consecutive Paragraphs

Paragraph 1

Paragraph 2

Paragraph 3

Paragraph 4

Paragraph 5

### Very Long Word

Pneumonoultramicroscopicsilicovolcanoconiosis is a long word.

Supercalifragilisticexpialidocious is another long word.

Incomprehensibilities is also quite long.

## Empty Code Blocks

```
```

```java
```

```python
```

## Multiple Consecutive Blank Lines





These paragraphs have multiple blank lines between them.



This should not cause issues.



## Links with Special Characters

[Link with spaces](https://example.com/test page.html)
[Link with underscores](https://example.com/test_file_name.html)
[Link with hyphens](https://example.com/test-file-name.html)
[Link with query params](https://example.com/search?q=test&lang=en)

## Lists with Edge Cases

### Empty List Items

- Item 1
-
- Item 3

### Single Item List

- Only one item

### Very Deep Nesting

- Level 1
  - Level 2
    - Level 3
      - Level 4
        - Level 5
          - Level 6
            - Level 7 (may not be supported)

### List with All Formatting

- **Bold** item
- *Italic* item
- ***Bold italic*** item
- Item with `inline code`
- Item with [link](https://example.com)
- Normal item at the end

## Code Edge Cases

### Inline Code with Special Characters

`code with "quotes"`
`code with 'single quotes'`
`code with backticks \` inside\``
`code with $pecial characters`

### Code Block with Tabs

```text
Line with	tab
```

### Code Block with Very Long Line

```java
public class VeryLongLine { public static void main(String[] args) { System.out.println("This is a very long line of code that should not break the conversion and should wrap properly in the Word document without any issues whatsoever"); } }
```

## Blockquote Edge Cases

### Empty Blockquote

>

### Blockquote with Only Special Characters

> ***

### Nested Empty Blockquotes

>
> >
> > >

## Formatting Edge Cases

### Unclosed Formatting

**bold without closing
*italic without closing
`code without closing

### Overlapping Formatting

**bold *italic** bold* (may not render correctly)

### Multiple Spaces

Multiple    spaces    between    words.

### Tabs at Start

	Tab-indented line

## Escaped Characters

\*Not bold\*
\_Not italic\_
\[Not a link\](not-a-link)
\`Not code\`

## Numeric Headings

### 1. Number as Heading

### 2. Another Number

### 100. Large Number

## Very Short Document

Short.

## URLs in Plain Text

Visit https://github.com for more info.
Go to https://example.com/page?id=123&ref=test.

**End of Edge Cases Test**
