# MarkDownToWord - MarkDown 转 Word 文档工具

## What We're Building

一个使用 Java 语言开发的 MarkDown 转 Word 文档转换工具。该工具能够将 MarkDown 的所有格式（标题、链接、斜体、加粗、表格、列表、警告、代码块等）完整转换为等价的 Word 富文本格式。

## Existing Codebase

这是一个全新的项目，目前没有任何现有代码。

## Tech Stack

- **Language**: Java
- **JDK Version**: 17.0.12 (local environment)
- **Build Tool**: Maven
- **Word Processing Library**: Apache POI (for .docx format)
- **Markdown Parsing**: CommonMark / Flexmark-java

## Project Structure

```
MarkDownToWord/
├── MarkDownToWordSource/     # 工具源码目录
│   ├── src/                  # Java 源码
│   │   ├── main/             # 主程序
│   │   └── test/             # 测试代码
│   ├── testFiles/            # 测试中间文件目录
│   ├── README.md             # 使用说明
│   ├── pom.xml               # Maven 配置
│   └── build/                # 构建产物
└── idea.md                   # 原始需求文档
```

## Commands

- **Build**: `cd MarkDownToWordSource && mvn clean package`
- **Test**: `cd MarkDownToWordSource && mvn test`
- **Run**: `java -jar MarkDownToWordSource/target/MarkDownToWord-1.0.jar <input.md> <output.docx>`

## Key Decisions

1. **Apache POI**: 作为 Word 文档生成库，是 Java 中处理 Office 文档的标准库
2. **CommonMark / Flexmark-java**: 用于解析 MarkDown 文档，支持 CommonMark 规范
3. **Maven**: 作为构建和依赖管理工具
4. **输出格式**: .docx (Office Open XML 格式)

## Verification Criteria

1. 转换为 Word 后，内容不能少
2. 转换为 Word 后，格式与 MarkDown 等价

## References

- `specs/SPECIFICATION.md` - 详细功能规格说明
- `idea.md` - 原始需求文档
