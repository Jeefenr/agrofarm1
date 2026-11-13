package agrofarm.report;

import agrofarm.OperationRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlReportGeneratorTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void rendersActualRecords() throws IOException {
        Path report = temporaryDirectory.resolve("operations.html");
        new HtmlReportGenerator().generate(report,
                List.of(new OperationRecord("OP-1", "оранка", "COMPLETED", "F-01")), List.of());

        String html = Files.readString(report);
        assertTrue(html.contains("OP-1"));
        assertTrue(html.contains("оранка"));
        assertTrue(html.contains("COMPLETED"));
        assertTrue(html.contains("F-01"));
    }

    @Test
    void rendersEmptyStateWithoutSyntheticRows() throws IOException {
        Path report = temporaryDirectory.resolve("empty.html");
        new HtmlReportGenerator().generate(report, List.of(), List.of());

        String html = Files.readString(report);
        assertTrue(html.contains("Записів не знайдено"));
        assertFalse(html.contains("<td>"));
    }

    @Test
    void escapesHtmlTextInAllRecordValues() throws IOException {
        Path report = temporaryDirectory.resolve("escaped.html");
        OperationRecord record = new OperationRecord("<op>&\"'", "тип <&\"'", "стан >&\"'", "поле <&\"'");
        new HtmlReportGenerator().generate(report, List.of(record), List.of());

        String html = Files.readString(report);
        assertTrue(html.contains("&lt;op&gt;&amp;&quot;&#39;"));
        assertTrue(html.contains("тип &lt;&amp;&quot;&#39;"));
        assertTrue(html.contains("стан &gt;&amp;&quot;&#39;"));
        assertTrue(html.contains("поле &lt;&amp;&quot;&#39;"));
    }

    @Test
    void highlightsSaveFailures() throws IOException {
        Path report = temporaryDirectory.resolve("warning.html");
        new HtmlReportGenerator().generate(report, List.of(), List.of("помилка збереження"));

        String html = Files.readString(report);
        assertTrue(html.contains("Попередження"));
        assertTrue(html.contains("помилка збереження"));
    }
}
