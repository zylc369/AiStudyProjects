# Architecture Specification

## Overview

This document describes the architecture for the Markdown to Word converter tool.

## Core Components

### 1. Markdown Parser (`parser` package)

**Responsibility**: Parse Markdown content into an abstract syntax tree (AST)

**Classes**:
- `MarkdownParser` - Main parser interface
- `MarkdownNode` - Represents a node in the Markdown AST
  - `NodeType`: HEADING, PARAGRAPH, LIST, TABLE, CODE_BLOCK, BLOCKQUOTE, LINK, TEXT, EMPHASIS
  - `children`: List of child nodes
  - `attributes`: Map of node-specific attributes (level, url, etc.)

**Input**: Raw Markdown string
**Output**: `MarkdownNode` tree

### 2. Word Document Writer (`writer` package)

**Responsibility**: Convert Markdown AST to Word document

**Classes**:
- `WordWriter` - Main writer interface
- `StyleManager` - Manages Word styles (fonts, colors, spacing)
- `TableWriter` - Handles table conversion
- `ListWriter` - Handles list conversion

**Input**: `MarkdownNode` tree
**Output`: Word document (.docx file)

### 3. Main Entry Point

**Class**: `MarkdownConverter`

**Responsibilities**:
- Parse command-line arguments
- Read input Markdown file
- Coordinate parser and writer
- Write output Word file

## Technology Choices

### Markdown Parsing Library

**Options**:
1. **flexmark-java** (Recommended)
   - Pros: Extensible, well-maintained, CommonMark compliant
   - Cons: May require custom visitor for AST walking
   - Dependency: `com.vladsch.flexmark:flexmark-all:0.64.8`

2. **commonmark-java**
   - Pros: Simple API, CommonMark compliant
   - Cons: Less extensible than flexmark
   - Dependency: `org.commonmark:commonmark:0.21.0`

**Decision**: Use flexmark-java for extensibility

### Word Generation Library

**Options**:
1. **Apache POI (XWPF)** (Recommended)
   - Pros: Widely used, stable, good documentation
   - Cons: Verbose API
   - Dependency: `org.apache.poi:poi-ooiful:5.2.5`

2. **docx4j**
   - Pros: More object-oriented, uses JAXB
   - Cons: Larger footprint, steeper learning curve
   - Dependency: `org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.9`

**Decision**: Use Apache POI XWPF for stability and community support

## Data Flow

```
Input .md file
    ↓
Read file content
    ↓
MarkdownParser → AST (MarkdownNode tree)
    ↓
WordWriter → XWPFDocument
    ↓
Write .docx file
    ↓
Output .docx file
```

## Error Handling

1. **File I/O errors**: Propagate with clear messages
2. **Invalid Markdown**: Parse gracefully, report line numbers
3. **Conversion errors**: Log and continue, don't fail entire document
