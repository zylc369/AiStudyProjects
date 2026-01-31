# Sample Markdown Document

This sample demonstrates all supported Markdown features for conversion to Word.

## Headers

Headers from H1 to H6 are supported:

# Heading 1
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6

## Text Formatting

Various text formatting options:

- **Bold text** using `**bold**` or `__bold__`
- *Italic text* using `*italic*` or `_italic_`
- ***Bold and italic*** using `***bolditalic***`
- ~~Strikethrough text~~ using `~~strikethrough~~`
- `Inline code` using backticks

## Links and Images

- Hyperlink: [Visit GitHub](https://github.com)
- Image placeholder: ![Sample Image](https://example.com/image.png)

## Lists

### Unordered Lists

- First item
- Second item
- Third item
  - Nested item 1
  - Nested item 2
    - Deep nested item
- Back to top level

### Ordered Lists

1. First numbered item
2. Second numbered item
3. Third numbered item
   1. Nested numbered item
   2. Another nested item
4. Continue with numbering

## Code Blocks

### Fenced Code Block

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### Indented Code Block

    This is an indented code block.
    It uses 4 spaces for indentation.

## Blockquotes

> This is a blockquote.
> It can span multiple lines.
>
> > Nested blockquotes are also supported.

## Horizontal Rules

Horizontal rules can be created with:

---

***

___

## Tables

| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Value 1  | Value 2  | Value 3  |
| Bold     | *Italic* | `Code`   |

## Combined Example

Here's a paragraph with **bold**, *italic*, and `code` all in one sentence.

> A blockquote with **formatted** text.

---

## Conversion Test

You can convert this file to Word using:

```bash
java -jar markdown-to-word-1.0.0.jar samples/sample.md output.docx
```

End of sample document.
