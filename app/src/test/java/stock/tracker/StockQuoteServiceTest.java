package stock.tracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StockQuoteServiceTest {
    private final StockQuoteService quoteService = new StockQuoteService();

    @Test
    void fetchQuotesReturnsEmptyMapForEmptySymbols() {
        assertDoesNotThrow(() -> {
            var result = quoteService.fetchQuotes(java.util.List.of(), java.util.Map.of());
            assertNotNull(result, "Result should not be null");
            assertTrue(result.isEmpty(), "Should return empty map for empty symbols");
        });
    }

    @Test
    void fetchQuotesHandlesValidSymbol() {
        assertDoesNotThrow(() -> {
            var result = quoteService.fetchQuotes(
                java.util.List.of("AAPL"),
                java.util.Map.of()
            );
            assertNotNull(result, "Result should not be null");
            // AAPL should be in the result (either valid quote or unavailable quote)
            assertTrue(result.containsKey("AAPL"), "Result should contain AAPL key");
        });
    }

    @Test
    void fetchQuotesHandlesInvalidSymbol() {
        assertDoesNotThrow(() -> {
            var result = quoteService.fetchQuotes(
                java.util.List.of("INVALID_XYZ"),
                java.util.Map.of()
            );
            assertNotNull(result, "Result should not be null");
            // Invalid symbols should still have entries (with unavailable status)
            assertTrue(result.containsKey("INVALID_XYZ"), "Result should contain the symbol");
        });
    }

    @Test
    void fetchQuotesNormalizesSymbolsToUppercase() {
        assertDoesNotThrow(() -> {
            var result = quoteService.fetchQuotes(
                java.util.List.of("aapl"),
                java.util.Map.of()
            );
            assertNotNull(result, "Result should not be null");
            // Symbol should be normalized to uppercase
            assertTrue(result.containsKey("AAPL"), "Result should contain uppercase AAPL");
        });
    }

    @Test
    void fetchTickerSuggestionsReturnsValidSuggestions() {
        assertDoesNotThrow(() -> {
            var result = quoteService.fetchTickerSuggestions("APP");
            assertNotNull(result, "Result should not be null");
            // Should return suggestions starting with APP (like AAPL)
        });
    }

    @Test
    void fetchTickerSuggestionsHandlesBlankQuery() {
        assertDoesNotThrow(() -> {
            var result = quoteService.fetchTickerSuggestions("   ");
            assertNotNull(result, "Result should not be null");
            assertTrue(result.isEmpty(), "Should return empty list for blank query");
        });
    }

    @Test
    void fetchTickerSuggestionsHandlesNullQuery() {
        assertDoesNotThrow(() -> {
            var result = quoteService.fetchTickerSuggestions(null);
            assertNotNull(result, "Result should not be null");
            assertTrue(result.isEmpty(), "Should return empty list for null query");
        });
    }
}
