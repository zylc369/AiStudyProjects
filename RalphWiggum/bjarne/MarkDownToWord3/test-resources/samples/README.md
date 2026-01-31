# Test Resources

This directory contains sample Markdown files for testing the Markdown to Word converter.

## Test Files

### basic-test.md
**Purpose:** Test basic Markdown elements

**Features Covered:**
- All heading levels (1-6)
- Paragraphs
- Text formatting (bold, italic, bold-italic)
- Links
- Inline code
- Horizontal rules

**Use Case:** Quick smoke test to verify basic conversion works.

---

### lists-test.md
**Purpose:** Test various list formats and nesting

**Features Covered:**
- Unordered lists (bulleted)
- Ordered lists (numbered)
- Nested lists (up to 3 levels)
- Mixed ordered/unordered lists
- Lists with text formatting
- Lists with inline code and links

**Use Case:** Verify list rendering and indentation works correctly.

---

### code-test.md
**Purpose:** Test code blocks and inline code

**Features Covered:**
- Inline code
- Fenced code blocks (triple backticks)
- Code blocks with language identifiers (Java, Python, JavaScript, Bash, JSON)
- Code blocks with empty lines
- Code blocks with special characters
- Multiple inline code in one line

**Use Case:** Verify monospace font and code block formatting.

---

### complete-test.md
**Purpose:** Comprehensive test with all features combined

**Features Covered:**
- All heading levels
- All text formatting combinations
- Links (various types and formats)
- Ordered and unordered lists (nested)
- Code blocks and inline code
- Blockquotes (simple and nested)
- Images (placeholder paths)
- Horizontal rules
- Complex combinations of all features

**Use Case:** Full integration test to verify complete document conversion.

---

### edge-cases-test.md
**Purpose:** Test edge cases and special scenarios

**Features Covered:**
- Empty headings
- Special characters (Unicode, emoji, HTML entities)
- Very long text and words
- Multiple consecutive blank lines
- Empty code blocks
- Links with special characters
- Lists with edge cases (empty items, single items, deep nesting)
- Code with special characters
- Unclosed formatting
- Escaped characters
- URLs in plain text

**Use Case:** Verify robustness and handling of unusual input.

---

## Usage

### Convert a Single Test File

```bash
cd MarkDownToWordSource
mvn clean compile
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.md2word.Main ../test-resources/samples/basic-test.md ../test-resources/samples/output/basic-test.docx
```

### Convert All Test Files

```bash
# Create output directory
mkdir -p test-resources/samples/output

# Convert each test file
for file in test-resources/samples/*-test.md; do
    name=$(basename "$file" .md)
    echo "Converting $name..."
    java -cp target/classes com.md2word.Main "$file" "test-resources/samples/output/${name}.docx"
done
```

---

## Expected Results

When conversion works correctly:

1. **basic-test.md** should produce a Word document with proper heading styles, text formatting, and clickable links.

2. **lists-test.md** should produce a Word document with proper list numbering/bullets and correct indentation for nested items.

3. **code-test.md** should produce a Word document with monospace font for code blocks and inline code.

4. **complete-test.md** should produce a Word document that preserves all formatting and structure without content loss.

5. **edge-cases-test.md** should produce a Word document that handles special characters, long text, and unusual input gracefully.

---

## Image Placeholders

Several test files reference image paths (e.g., `samples/test-image.png`). These are placeholder paths.

To test actual image conversion:

1. Create the `samples/` directory
2. Add actual image files (PNG, JPG, GIF, BMP)
3. Update image paths in test files to match your image files

---

## Verification Checklist

After conversion, verify the Word document:

- [ ] All headings render with correct styles (Heading 1-6)
- [ ] Text formatting (bold, italic) matches source
- [ ] Links are clickable
- [ ] Lists maintain structure and indentation
- [ ] Code blocks use monospace font
- [ ] Blockquotes have distinct formatting
- [ ] No content is missing
- [ ] No unexpected formatting errors
- [ ] Document opens without errors in Word/LibreOffice

---

## Notes

- All test files use standard Markdown syntax (CommonMark + GitHub Flavored Markdown)
- Horizontal rules (`---`, `***`, `___`) are included but may not be supported (requires flexmark extension)
- Image paths are placeholders and may cause conversion errors if files don't exist (this is expected behavior)
