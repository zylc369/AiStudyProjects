# Complete Markdown Feature Test

This file demonstrates all supported Markdown features in a single document.

## Document Structure

This test file contains **all supported features** to verify *complete* conversion functionality.

---

## Headings

### All Heading Levels

# Heading Level 1
## Heading Level 2
### Heading Level 3
#### Heading Level 4
##### Heading Level 5
###### Heading Level 6

## Text Formatting

This paragraph demonstrates **bold text**, *italic text*, and ***bold italic text***.

You can also use __bold text__, _italic text_, and ___bold italic text___.

### Formatting Combinations

Here is a paragraph with **bold and `inline code`** together.

Here is *italic with [a link](https://github.com)* inside.

Here is ***bold italic with `code` and a [link](https://example.com)*** all combined.

## Links

### Various Link Types

- [Link to GitHub](https://github.com)
- [Link to relative file](./other-file.md)
- [Link with underscore in URL](https://example.com/test_file.html)
- [Link to anchor](#heading-level-1)

### Link with Formatting

- [**Bold link text**](https://example.com)
- [*Italic link text*](https://example.com)
- [***Bold italic link***](https://example.com)

## Lists

### Unordered Lists

- First item
- Second item
  - Nested item
  - Another nested item
- Third item

### Ordered Lists

1. First step
2. Second step
   1. Sub-step 2.1
   2. Sub-step 2.2
3. Third step

### Mixed Lists

1. Ordered item
   - Unordered nested item
   - Another unordered item
     1. Deep ordered item
     2. Another deep ordered item
2. Back to ordered

## Code Blocks

### Inline Code Examples

Use the `public static void main` method to start a Java program.

The `import` statement comes at the top of the file.

### Fenced Code Blocks

#### Java Example

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

#### Python Example

```python
def add(a, b):
    return a + b
```

#### JavaScript Example

```javascript
function add(a, b) {
    return a + b;
}
```

## Blockquotes

### Simple Blockquote

> This is a simple blockquote.
> It can span multiple lines.

### Blockquote with Formatting

> **Important:** This is a blockquote with **bold text**.
>
> You can also include *italic text* and `inline code`.

### Nested Blockquotes

> This is the outer blockquote.
>
> > This is a nested blockquote.
> >
> > > This is a triple-nested blockquote.

### Blockquote with Lists

> This blockquote contains a list:
> - Item 1
> - Item 2
> - Item 3

## Images

> **Note:** Image paths are placeholders. Replace with actual image files for testing.

![Sample Image](samples/test-image.png)

### Image with Alt Text

![Logo](samples/logo.png)

### Image with Long Alt Text

![A beautiful sunset over the ocean waves](samples/sunset.jpg)

## Horizontal Rules

***

---

___

## Complex Combinations

### Blockquote with Code

> Here is a blockquote with code:
>
> ```java
> System.out.println("Hello");
> ```

### List with Code

- Item with `inline code`
- Another item
- Item with code block:

  ```python
  print("Hello")
  ```

### Heading with All Features

### **Complex Section** with *everything* combined

This paragraph has **bold**, *italic*, `inline code`, and [links](https://example.com).

> A blockquote to add variety.

- And a list for good measure

```java
// And a code block
public class Complete {
    String text = "All features!";
}
```

---

## Final Test

This document tests **all supported features**. If conversion works correctly, the resulting Word document should preserve:

✅ All heading levels (1-6)
✅ Text formatting (bold, italic, bold-italic)
✅ Links (various URLs)
✅ Lists (ordered, unordered, nested)
✅ Code blocks and inline code
✅ Blockquotes (including nested)
✅ Images (placeholder paths)
✅ Horizontal rules

**End of Complete Test**
