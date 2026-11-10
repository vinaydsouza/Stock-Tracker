package stock.tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StockCsvExporterTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void exportsHeadersAndRowsAsUtf8CsvWithEscapedFields() throws Exception {
        Path destination = temporaryDirectory.resolve("quotes.csv");

        StockCsvExporter.export(destination, List.of("Symbol", "Name", "Note"), List.of(
                List.of("AAPL", "Apple, Inc.", "Said \"hello\"\nworld")
        ));

        assertEquals(
                "Symbol,Name,Note\r\nAAPL,\"Apple, Inc.\",\"Said \"\"hello\"\"\nworld\"\r\n",
                Files.readString(destination, StandardCharsets.UTF_8)
        );
    }

    @Test
    void rejectsRowsThatDoNotMatchHeaderWidth() {
        Path destination = temporaryDirectory.resolve("invalid.csv");

        assertThrows(IllegalArgumentException.class,
                () -> StockCsvExporter.export(destination, List.of("Symbol", "Price"), List.of(List.of("AAPL"))));
    }
}
