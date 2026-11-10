package stock.tracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class StockCsvExporter {
    private StockCsvExporter() {
    }

    static void export(Path destination, List<String> headers, List<List<String>> rows) throws IOException {
        StringBuilder csv = new StringBuilder();
        appendRecord(csv, headers);
        for (List<String> row : rows) {
            if (row.size() != headers.size()) {
                throw new IllegalArgumentException("CSV row width must match the header width.");
            }
            appendRecord(csv, row);
        }
        Files.writeString(destination, csv.toString(), StandardCharsets.UTF_8);
    }

    private static void appendRecord(StringBuilder csv, List<String> fields) {
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                csv.append(',');
            }
            String value = fields.get(i) == null ? "" : fields.get(i);
            boolean quote = value.contains(",") || value.contains("\"") || value.contains("\r") || value.contains("\n");
            if (quote) {
                csv.append('"').append(value.replace("\"", "\"\"")).append('"');
            } else {
                csv.append(value);
            }
        }
        csv.append("\r\n");
    }
}
