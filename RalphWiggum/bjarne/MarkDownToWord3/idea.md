# 生成 MarkDown 文件转 Word 文档的工具

## 目标
生成 MarkDown 文件转 Word 文档的工具

## 技术栈要求
1. 要求使用 Java 语言。
2. JDK：当前电脑的环境，JDK 版本是 17.0.12。

## 需求
1. MarkDown 转 Word，要转换为富文本。这意味着不是将 MarkDown 的文本原样转换为 Word，而是要将 MarkDown 支持的所有格式，如：的标题、链接、文字斜体、文字加粗、表格、列表、警告、代码块等，都要转换成等价的 Word 格式。
2. 要将 MarkDown 完整的内容转换到 Word。
3. 在工具源码目录内生成 README.md 文件，告诉用户如何使用这个工具。
4. 要写测试用例，确保工具正确性。

## 代码存放位置
代码放到**MarkDownToWordSource**目录下，这个目录是工具源码目录。

## 验证要求
1. 要求能编译成功。
2. 工具将MarkDown文件转换为 Word 后，内容不能少。
3. 工具将MarkDown文件转换为 Word 后，格式与 MarkDown 等价。
