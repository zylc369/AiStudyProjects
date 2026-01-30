# Implementation Tasks

## Project Setup
- [ ] Create MarkDownToWordSource directory structure → MarkDownToWordSource/src/main/java/com/markdown/toword/{model,parser,converter} and src/test/java directories exist
- [x] Create pom.xml with required dependencies → pom.xml includes Apache POI 5.2.5, Flexmark 0.64.8, JUnit 5.10.1, AssertJ 3.24.2
- [ ] Configure Maven for Java 17 → pom.xml includes maven-compiler-plugin with source/target 17 and UTF-8 encoding

## Core Infrastructure
- [x] Create ConversionException class → Custom exception class in model package with message and cause constructors
- [ ] Create MarkdownParser class → Parses Markdown string to Flexmark AST Document using Parser.builder().build()
- [ ] Create MarkdownConverter facade class → Main class with convert(String markdown, String outputPath) and convertFile(String inputPath, String outputPath) methods
- [ ] Implement XWPFDocument creation → Creates empty Word document with proper POI XWPFDocument initialization

## Inline Text Formatting (Foundation)
- [ ] Create InlineTextConverter class → Handles bold, italic, strikethrough, inline code within text runs
- [ ] Implement bold text conversion → Markdown **bold** or __bold__ converts to XWPFRun with setBold(true)
- [ ] Implement italic text conversion → Markdown *italic* or _italic_ converts to XWPFRun with setItalic(true)
- [ ] Implement strikethrough conversion → Markdown ~~text~~ converts to XWPFRun with setStrikeThrough(true)
- [ ] Implement inline code conversion → Markdown `code` converts to XWPFRun with Courier New font, size 10

## Header Conversion
- [ ] Implement HeaderConverter class → Converts Markdown headers to Word heading styles
- [ ] Implement H1-H6 style mapping → Maps levels 1-6 to Word styles "Heading1" through "Heading6"
- [ ] Support inline formatting in headers → Headers can contain bold/italic within text

## Paragraph Conversion
- [ ] Implement ParagraphConverter class → Converts Markdown paragraphs to Word paragraphs
- [ ] Skip heading nodes in paragraphs → Headers handled separately by HeaderConverter
- [ ] Support inline formatting in paragraphs → Paragraphs can contain bold/italic/code

## List Conversion
- [ ] Implement ListConverter class → Converts Markdown lists to Word numbered/bulleted lists
- [ ] Implement unordered list conversion → Markdown -/*/+ items convert to Word bullets with XWPFNumbering NUM_FMT.BULLET
- [ ] Implement ordered list conversion → Markdown 1. items convert to Word decimal numbering
- [ ] Handle nested list indentation → Nested lists have proper indentation level in Word

## Link Conversion
- [ ] Implement LinkConverter class → Converts Markdown links to Word hyperlinks
- [ ] Create XWPFHyperlinkRun for links → Markdown [text](url) creates hyperlink with display text and URL target

## Code Block Conversion
- [ ] Implement CodeConverter class → Converts fenced and indented code blocks
- [ ] Implement fenced code block conversion → Markdown ```code``` converts to paragraph with Courier New font, 9pt, gray background RGB(245,245,245)
- [ ] Preserve code whitespace → Leading/trailing spaces in code blocks are preserved exactly

## Table Conversion
- [ ] Implement TableConverter class → Converts Markdown tables to Word XWPFTable
- [ ] Create table structure → Proper XWPFTable with rows and cells from Markdown table syntax
- [ ] Format header row → First row has bold text and gray background RGB(217,217,217)
- [ ] Handle column alignment → Left/center/right alignment from Markdown :---: markers

## Blockquote Conversion
- [ ] Implement BlockquoteConverter class → Converts Markdown blockquotes
- [ ] Apply blockquote formatting → Left indentation 720 twips, italic text style

## Horizontal Rule Conversion
- [ ] Implement HorizontalRuleConverter class → Converts Markdown --- or *** to horizontal line
- [ ] Add paragraph bottom border → Uses setBorderBottom(Borders.SINGLE) for horizontal rule appearance

## Image Conversion
- [ ] Implement ImageConverter class → Converts Markdown images to embedded pictures
- [ ] Support absolute path images → ![alt](/path/to.png) embeds image from absolute path
- [ ] Support relative path images → Resolves image paths relative to Markdown file location
- [ ] Set alt text on images → Picture description set from alt text in Markdown

## CLI Entry Point
- [ ] Create Main class with main method → Accepts command-line arguments for input/output files
- [ ] Add argument parsing → Validates input file exists, handles output path, auto-generates .docx filename if not specified
- [ ] Add error handling for CLI → User-friendly error messages for missing files or conversion failures

## Documentation
- [ ] Create README.md in MarkDownToWordSource → README includes project description, requirements, build instructions, usage examples, and feature list

## Test Infrastructure
- [ ] Create test base class → MarkdownConverterTestBase with temp directory setup and converter initialization
- [ ] Create test resource directories → src/test/resources/markdown/ and src/test/resources/images/ exist

## Unit Tests
- [ ] Test header conversion → Verifies # Header converts to Word Heading1 style
- [ ] Test paragraph conversion → Verifies paragraph text and inline formatting
- [ ] Test bold text conversion → Verifies **bold** creates run with bold=true
- [ ] Test italic text conversion → Verifies *italic* creates run with italic=true
- [ ] Test strikethrough conversion → Verifies ~~strike~~ creates run with strikeThrough=true
- [ ] Test inline code conversion → Verifies `code` uses Courier New font size 10
- [ ] Test unordered list conversion → Verifies - items create bulleted list
- [ ] Test ordered list conversion → Verifies 1. items create numbered list
- [ ] Test nested list conversion → Verifies nested lists have proper indentation
- [ ] Test link conversion → Verifies [text](url) creates functional hyperlink
- [ ] Test code block conversion → Verifies ```code``` has monospace font and gray background
- [ ] Test table conversion → Verifies table structure, header formatting, and cell content
- [ ] Test blockquote conversion → Verifies left indentation and italic formatting
- [ ] Test horizontal rule conversion → Verifies bottom border applied
- [ ] Test image conversion → Verifies image embedded with alt text

## Integration Tests
- [ ] Test complex document conversion → Verifies document with all element types converts without errors
- [ ] Test content completeness → Extracts all text from output and verifies no content loss
- [ ] Test output file validity → Verifies output .docx can be opened with Apache POI

## Build & Verification
- [ ] Compile project with Maven → `mvn clean package` completes successfully with no compilation errors
- [ ] Run all tests → `mvn test` passes all test cases
- [ ] Generate executable JAR → Package with dependencies for distribution
- [ ] Manual verification test → Convert sample Markdown to Word and verify formatting in Word application
