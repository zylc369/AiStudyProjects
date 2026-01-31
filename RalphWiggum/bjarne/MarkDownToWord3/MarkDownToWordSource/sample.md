# Markdown to Word Converter - Sample Document

This sample document demonstrates all the Markdown features that are supported by the Markdown to Word Converter.

You can convert this file to a Word document by running:

```bash
java -cp target/classes com.md2word.Main sample.md sample-output.docx
```

---

## Headings

The converter supports all 6 heading levels:

### Heading Level 3
#### Heading Level 4
##### Heading Level 5
###### Heading Level 6

Headings are converted to Word with the appropriate heading styles (Heading1 through Heading6).

---

## Text Formatting

The converter supports **bold**, *italic*, and ***bold italic*** text.

You can use alternative syntax as well: __bold__, _italic_, and ___bold italic___.

### Examples

- **Bold text**: This text is **bold** and stands out.
- *Italic text*: This text is *italic* and emphasizes the content.
- **Bold italic**: This text is ***bold and italic*** for extra emphasis.

Mixed formatting within a paragraph is also supported. You can have **bold** and *italic* text in the same sentence, or even combine them like ***this***.

---

## Links

Links are converted to clickable hyperlinks in the Word document.

### External Links

- Visit [GitHub](https://github.com) for open-source projects
- Learn more about [Markdown](https://www.markdownguide.org/)
- Apache POI documentation is available [here](https://poi.apache.org/)

### Link with Title

You can add titles to links: [OpenAI](https://openai.com "OpenAI Homepage")

---

## Lists

The converter supports both ordered and unordered lists, including nested lists.

### Unordered Lists

Bullet points use the `-`, `*`, or `+` character:

- First item
- Second item
- Third item
  - Nested item using indentation
  - Another nested item
- Back to first level
- Fifth item

### Ordered Lists

Numbered lists use digits followed by a period:

1. First step
2. Second step
3. Third step
   1. Nested numbered item
   2. Another nested item
4. Fourth step

### Mixed Lists

You can even mix ordered and unordered lists:

1. Ordered item
   - Unordered nested item
   - Another nested item
2. Back to ordered
3. Final ordered item

---

## Code Blocks

The converter supports both fenced code blocks and inline code.

### Fenced Code Blocks

Fenced code blocks are surrounded by triple backticks:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

```python
def hello():
    print("Hello from Python!")
```

### Inline Code

You can use `inline code` within a paragraph to highlight code snippets or technical terms.

For example, use the `mvn package` command to build the project.

---

## Blockquotes

Blockquotes are used to highlight important information or quotes.

> This is a blockquote. It is formatted with italic text and indentation in the Word document.
>
> You can have multiple paragraphs in a blockquote.

> **Note**: Blockquotes can contain other Markdown formatting like **bold** and *italic* text.

---

## Images

Images are embedded in the Word document from local file paths.

> **Note**: For this example to work, replace the path below with an actual image file on your system.

![Sample Image](path/to/your/image.png)

You can also add alt text and titles:

![Alt text description](path/to/image.jpg "Optional title")

---

## Complete Example

Here's a section that combines multiple features:

# Project Documentation

## Installation

To install the required dependencies, run:

```bash
npm install markdown-to-word-converter
```

## Features

The converter supports:

- **Headings** at all 6 levels
- *Text formatting* (bold, italic, bold-italic)
- [Clickable links](https://example.com)
1. Ordered lists
2. Unordered lists
   - With nesting support

> **Tip**: Visit [Markdown Guide](https://www.markdownguide.org/) to learn more about Markdown syntax.

---

## Testing the Converter

To convert this file to Word, navigate to the `MarkDownToWordSource` directory and run:

```bash
# Compile the project
mvn clean compile

# Convert this sample file
java -cp target/classes com.md2word.Main sample.md sample-output.docx
```

Then open `sample-output.docx` in Microsoft Word or any compatible word processor to see the results!

---

## Limitations

The following Markdown features are **not currently supported**:

- Tables (require flexmark-ext-tables extension)
- Horizontal rules (require flexmark GFM extensions)
- Task lists
- Footnotes

These features may be added in future releases.

---

*End of sample document*
