package agrofarm.report;

import agrofarm.OperationRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HtmlReportGenerator {
    public void generate(Path reportPath, List<OperationRecord> records, List<String> saveErrors)
            throws IOException {
        StringBuilder html = new StringBuilder("""
                <!doctype html>
                <html lang="uk">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Технічні операції</title>
                  <style>
                    body { font-family: sans-serif; margin: 2rem auto; max-width: 70rem; padding: 0 1rem; }
                    table { border-collapse: collapse; width: 100%; }
                    th, td { border: 1px solid #999; padding: .5rem; text-align: left; }
                    th { background: #e8f2e3; }
                    .warning { background: #fff3cd; border: 1px solid #856404; padding: 1rem; }
                  </style>
                </head>
                <body>
                  <h1>Технічні операції</h1>
                """);

        if (!saveErrors.isEmpty()) {
            html.append("<section class=\"warning\" role=\"alert\"><h2>Попередження: не всі операції збережено</h2><ul>");
            for (String error : saveErrors) {
                html.append("<li>").append(escapeHtml(error)).append("</li>");
            }
            html.append("</ul></section>");
        }

        if (records.isEmpty()) {
            html.append("<p>Записів не знайдено.</p>");
        } else {
            html.append("""
                      <table>
                        <thead>
                          <tr><th>ID операції</th><th>Тип</th><th>Стан</th><th>ID поля</th></tr>
                        </thead>
                        <tbody>
                    """);
            for (OperationRecord record : records) {
                html.append("<tr><td>").append(escapeHtml(record.operationId()))
                        .append("</td><td>").append(escapeHtml(record.operationType()))
                        .append("</td><td>").append(escapeHtml(record.status()))
                        .append("</td><td>").append(escapeHtml(record.fieldId()))
                        .append("</td></tr>");
            }
            html.append("</tbody></table>");
        }

        html.append("</body></html>\n");
        Files.writeString(reportPath, html, StandardCharsets.UTF_8);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
