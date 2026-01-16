# MarkDownToWord 功能规格说明

## 1. 概述

本工具是一个 Java 命令行应用程序，用于将 MarkDown 文件转换为 Word 文档（.docx 格式）。转换过程中保留所有 MarkDown 格式，生成等价的 Word 富文本格式。

## 2. 技术规格

### 2.1 输入规格

- **输入格式**: MarkDown 文件 (.md)
- **编码**: UTF-8
- **支持的 MarkDown 元素**:
  - 标题 (H1-H6)
  - 段落
  - 文本格式（加粗、斜体、删除线）
  - 链接
  - 图片
  - 行内代码
  - 代码块（带语法高亮支持）
  - 无序列表
  - 有序列表
  - 嵌套列表
  - 表格
  - 引用块
  - 分隔线
  - 任务列表（待办事项）

### 2.2 输出规格

- **输出格式**: Word 文档 (.docx)
- **Word 格式映射**:
  | MarkDown 元素 | Word 格式 |
  |--------------|-----------|
  | # H1 | Heading 1 样式 |
  | ## H2 | Heading 2 样式 |
  | ### H3 | Heading 3 样式 |
  | #### H4 | Heading 4 样式 |
  | ##### H5 | Heading 5 样式 |
  | ###### H6 | Heading 6 样式 |
  | **加粗** | 粗体文字 |
  | *斜体* | 斜体文字 |
  | ~~删除线~~ | 删除线文字 |
  | [链接文本](url) | 超链接 |
  | `行内代码` | 等宽字体 |
  | ```代码块``` | 预格式化文本块 |
  | - 无序列表 | 项目符号列表 |
  | 1. 有序列表 | 编号列表 |
  | > 引用 | 引用格式 |
  | --- 分隔线 | 水平线 |

### 2.3 命令行接口

```
java -jar MarkDownToWord.jar <input.md> [output.docx]
```

**参数说明**:
- `<input.md>`: 必需，输入的 MarkDown 文件路径
- `[output.docx]`: 可选，输出的 Word 文件路径。如果不指定，则使用输入文件名替换扩展名为 .docx

**返回值**:
- 0: 转换成功
- 1: 转换失败（错误信息输出到 stderr）

## 3. 功能需求

### 3.1 核心功能

#### FR-1: MarkDown 解析
系统必须能够正确解析符合 CommonMark 规范的 MarkDown 文档，生成抽象语法树（AST）。

#### FR-2: Word 文档生成
系统必须能够生成符合 Office Open XML (.docx) 格式的 Word 文档。

#### FR-3: 格式转换映射
系统必须将 MarkDown 的各种格式元素正确映射到对应的 Word 格式。

### 3.2 质量需求

#### QR-1: 内容完整性
转换后的 Word 文档必须包含原 MarkDown 文档的所有文本内容，不允许有任何内容丢失。

#### QR-2: 格式等价性
转换后的 Word 文档的格式必须与原 MarkDown 文档的格式语义等价。

#### QR-3: 字符编码
系统必须正确处理 UTF-8 编码的字符，包括中文、表情符号等特殊字符。

### 3.3 非功能需求

#### NFR-1: 性能
- 转换 1MB 的 MarkDown 文件应在 5 秒内完成
- 支持最大文件大小：100MB

#### NFR-2: 可靠性
- 优雅处理文件读取错误（文件不存在、权限不足等）
- 优雅处理 MarkDown 解析错误
- 生成有效的 Word 文档，确保能在 Microsoft Word 中打开

#### NFR-3: 可测试性
- 提供完整的单元测试覆盖
- 测试中间文件必须放在 testFiles 目录下

## 4. 测试用例规格

### 4.1 基础格式测试

**测试文件**: test-basic-formats.md
```
# H1 标题
## H2 标题
### H3 标题

这是一个普通段落。

**粗体文字** 和 *斜体文字*。

***粗斜体文字***

~~删除线文字~~

[链接文本](https://example.com)

`行内代码`

```java
// 代码块
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

---

> 这是引用块

- 无序列表项 1
- 无序列表项 2
  - 嵌套列表项

1. 有序列表项 1
2. 有序列表项 2
```

**验证点**:
- [ ] 所有标题样式正确应用
- [ ] 文本格式（粗体、斜体、删除线）正确显示
- [ ] 链接可点击且指向正确 URL
- [ ] 代码块保留原始格式
- [ ] 列表层级正确
- [ ] 引用块样式正确

### 4.2 表格测试

**测试文件**: test-tables.md
```
| 列1 | 列2 | 列3 |
|-----|-----|-----|
| 数据1 | 数据2 | 数据3 |
| 数据4 | 数据5 | 数据6 |
```

**验证点**:
- [ ] 表格正确创建
- [ ] 表头样式正确
- [ ] 单元格内容完整

### 4.3 复杂文档测试

**测试文件**: test-complex.md
包含上述所有元素的复杂文档。

**验证点**:
- [ ] 所有元素正确转换
- [ ] 文档结构完整
- [ ] 无内容丢失

### 4.4 内容完整性测试

**验证方法**:
1. 提取原 MarkDown 文件的所有纯文本内容
2. 提取生成的 Word 文档的所有文本内容
3. 比较两者，确保内容完全一致（忽略格式标记）

### 4.5 边界情况测试

- 空文件
- 只有标题的文件
- 非常长的段落
- 深层嵌套列表
- 特殊字符（emoji、数学符号等）

## 5. 项目结构

```
MarkDownToWordSource/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── markdowntoword/
│   │               ├── Main.java              # 主入口类
│   │               ├── parser/
│   │               │   └── MarkdownParser.java # MarkDown 解析器
│   │               ├── converter/
│   │               │   ├── WordDocumentBuilder.java # Word 文档构建器
│   │               │   ├── HeadingConverter.java    # 标题转换器
│   │               │   ├── TextConverter.java        # 文本格式转换器
│   │               │   ├── ListConverter.java       # 列表转换器
│   │               │   ├── TableConverter.java      # 表格转换器
│   │               │   └── CodeConverter.java       # 代码转换器
│   │               └── exception/
│   │                   └── ConversionException.java  # 转换异常
│   └── test/
│       └── java/
│           └── com/
│               └── markdowntoword/
│                   ├── MarkdownParserTest.java
│                   ├── WordDocumentBuilderTest.java
│                   ├── FormatConverterTest.java
│                   └── IntegrationTest.java
├── testFiles/                           # 测试中间文件目录
│   ├── test-basic-formats.md           # 测试用 MarkDown 文件
│   ├── test-tables.md
│   ├── test-complex.md
│   ├── test-basic-formats.docx         # 生成的 Word 文件
│   ├── test-tables.docx
│   └── test-complex.docx
├── target/                             # Maven 构建目录
│   ├── classes/
│   └── MarkDownToWord-1.0.jar          # 最终 JAR 文件
├── pom.xml                             # Maven 配置
└── README.md                           # 使用说明
```

## 6. 依赖库

### 6.1 Apache POI
用于生成和操作 Office 文档：
- `poi`: 核心库
- `poi-ooxml`: OOXML 格式支持 (.docx)

### 6.2 Flexmark-java
用于解析 MarkDown 文档：
- `flexmark-all`: 完整的 CommonMark 解析器

## 7. 验收标准

### 7.1 功能验收
- [ ] 所有 MarkDown 元素正确转换
- [ ] 转换后的 Word 文档可在 Microsoft Word 中正常打开和编辑
- [ ] 命令行参数正确处理

### 7.2 质量验收
- [ ] 单元测试覆盖率 ≥ 80%
- [ ] 所有测试用例通过
- [ ] 无内容丢失
- [ ] 格式等价

### 7.3 交付物
- [ ] 源代码（位于 MarkDownToWordSource 目录）
- [ ] 可执行的 JAR 文件
- [ ] README.md 使用说明文档
- [ ] 测试用例和测试文件
