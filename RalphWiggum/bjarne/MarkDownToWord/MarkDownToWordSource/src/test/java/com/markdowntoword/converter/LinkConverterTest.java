package com.markdowntoword.converter;

import com.markdowntoword.parser.MarkdownParser;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LinkConverter.
 */
class LinkConverterTest {

    @TempDir
    Path tempDir;

    private XWPFDocument document;

    @AfterEach
    void cleanup() throws IOException {
        if (document != null) {
            document.close();
        }
    }

    @Test
    void constructor_initializesDocument() throws IOException {
        XWPFDocument doc = new XWPFDocument();
        LinkConverter converter = new LinkConverter(doc);

        assertNotNull(converter.getDocument());
        assertEquals(doc, converter.getDocument());

        doc.close();
    }

    @Test
    void constructor_withNullDocument_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new LinkConverter(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void addHyperlink_withSimpleText_createsParagraphWithHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("Example", "https://example.com");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Example", getHyperlinkText(paragraph));
    }

    @Test
    void addHyperlink_withLongText_createsParagraphWithHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        String longText = "This is a very long link text that spans multiple words and should be handled correctly";
        converter.addHyperlink(longText, "https://example.com");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals(longText, getHyperlinkText(paragraph));
    }

    @Test
    void addHyperlink_withNullText_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHyperlink(null, "https://example.com")
        );

        assertEquals("Hyperlink text cannot be null", exception.getMessage());
    }

    @Test
    void addHyperlink_withNullUrl_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHyperlink("Example", null)
        );

        assertEquals("Hyperlink URL cannot be null", exception.getMessage());
    }

    @Test
    void addHyperlink_withBlankUrl_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.addHyperlink("Example", "")
        );

        assertEquals("Hyperlink URL cannot be blank", exception.getMessage());
    }

    @Test
    void addHyperlink_withEmptyText_createsParagraphWithHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("", "https://example.com");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("", getHyperlinkText(paragraph));
    }

    @Test
    void addHyperlink_withSpecialCharacters_createsParagraphWithHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("Test & Link", "https://example.com?param=value&other=123");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Test & Link", getHyperlinkText(paragraph));
    }

    @Test
    void addHyperlink_withMultipleHyperlinks_sequentially() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("First Link", "https://example.com/first");
        converter.addHyperlink("Second Link", "https://example.com/second");
        converter.addHyperlink("Third Link", "https://example.com/third");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(3, paragraphs.size());

        assertEquals("First Link", getHyperlinkText(paragraphs.get(0)));
        assertEquals("Second Link", getHyperlinkText(paragraphs.get(1)));
        assertEquals("Third Link", getHyperlinkText(paragraphs.get(2)));
    }

    @Test
    void convertLink_withSimpleLink_createsHyperlinkParagraph() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("[Example](https://example.com)");
        Link link = (Link) doc.getFirstChild().getFirstChild();

        converter.convertLink(link);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Example", getHyperlinkText(paragraph));
    }

    @Test
    void convertLink_withLongLinkText_createsHyperlinkParagraph() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        Parser parser = Parser.builder(new MutableDataSet()).build();
        com.vladsch.flexmark.util.ast.Document doc = parser.parse("[This is a long link text](https://example.com)");
        Link link = (Link) doc.getFirstChild().getFirstChild();

        converter.convertLink(link);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("This is a long link text", getHyperlinkText(paragraph));
    }

    @Test
    void convertLink_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertLink(null)
        );

        assertEquals("Link cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMultipleLinks_convertsAllLinks() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "[First Link](https://example.com/first)\n" +
            "[Second Link](https://example.com/second)"
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(2, paragraphs.size());

        assertEquals("First Link", getHyperlinkText(paragraphs.get(0)));
        assertEquals("Second Link", getHyperlinkText(paragraphs.get(1)));
    }

    @Test
    void convertDocument_withNoLinks_createsNoParagraphs() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "This is just regular text.\n" +
            "No links here.\n" +
            "Just paragraphs."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(0, paragraphs.size());
    }

    @Test
    void convertDocument_withNull_throwsIllegalArgumentException() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> converter.convertDocument(null)
        );

        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    void convertDocument_withMixedContent_convertsOnlyLinks() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        MarkdownParser parser = new MarkdownParser();
        com.vladsch.flexmark.util.ast.Document markdownDocument = parser.parse(
            "Some introductory text.\n" +
            "[First Link](https://example.com/first)\n" +
            "More text here.\n" +
            "[Second Link](https://example.com/second)\n" +
            "Concluding paragraph."
        );

        converter.convertDocument(markdownDocument);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(2, paragraphs.size());

        assertEquals("First Link", getHyperlinkText(paragraphs.get(0)));
        assertEquals("Second Link", getHyperlinkText(paragraphs.get(1)));
    }

    @Test
    void savedDocument_reopened_hyperlinkIsPreserved() throws IOException {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("Example", "https://example.com");

        Path testFile = tempDir.resolve("test-hyperlink.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(1, paragraphs.size());

            XWPFParagraph paragraph = paragraphs.get(0);
            assertEquals("Example", getHyperlinkText(paragraph));
        }
    }

    @Test
    void savedDocument_reopened_multipleHyperlinksArePreserved() throws IOException {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("First Link", "https://example.com/first");
        converter.addHyperlink("Second Link", "https://example.com/second");

        Path testFile = tempDir.resolve("test-multiple-hyperlinks.docx");
        try (FileOutputStream outputStream = new FileOutputStream(testFile.toFile())) {
            document.write(outputStream);
        }
        document.close();

        try (XWPFDocument reopenedDocument = new XWPFDocument(new FileInputStream(testFile.toFile()))) {
            List<XWPFParagraph> paragraphs = reopenedDocument.getParagraphs();
            assertEquals(2, paragraphs.size());

            assertEquals("First Link", getHyperlinkText(paragraphs.get(0)));
            assertEquals("Second Link", getHyperlinkText(paragraphs.get(1)));
        }
    }

    @Test
    void addHyperlink_withHttpUrl_createsHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("HTTP Link", "http://example.com");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("HTTP Link", getHyperlinkText(paragraph));
    }

    @Test
    void addHyperlink_withHttpsUrl_createsHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("HTTPS Link", "https://example.com");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("HTTPS Link", getHyperlinkText(paragraph));
    }

    @Test
    void addHyperlink_withFtpUrl_createsHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("FTP Link", "ftp://example.com/file.txt");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("FTP Link", getHyperlinkText(paragraph));
    }

    @Test
    void addHyperlink_withMailtoUrl_createsHyperlink() {
        document = new XWPFDocument();
        LinkConverter converter = new LinkConverter(document);

        converter.addHyperlink("Email", "mailto:test@example.com");

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertEquals(1, paragraphs.size());

        XWPFParagraph paragraph = paragraphs.get(0);
        assertEquals("Email", getHyperlinkText(paragraph));
    }

    /**
     * Helper method to extract text from hyperlink runs in a paragraph.
     * This is needed because paragraph.getText() doesn't include text from XWPFHyperlinkRun.
     *
     * @param paragraph the paragraph to extract hyperlink text from
     * @return the concatenated text from all hyperlink runs in the paragraph
     */
    private String getHyperlinkText(XWPFParagraph paragraph) {
        StringBuilder text = new StringBuilder();

        // Access the underlying XML to extract hyperlink text
        CTHyperlink[] hyperlinks = paragraph.getCTP().getHyperlinkArray();
        if (hyperlinks != null) {
            for (CTHyperlink hyperlink : hyperlinks) {
                CTR[] runs = hyperlink.getRArray();
                if (runs != null) {
                    for (CTR run : runs) {
                        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] textElements = run.getTArray();
                        if (textElements != null) {
                            for (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText textElement : textElements) {
                                text.append(textElement.getStringValue());
                            }
                        }
                    }
                }
            }
        }

        return text.toString();
    }
}
