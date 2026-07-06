package stock.tracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

class StockNewsServiceTest {
    private final StockNewsService newsService = new StockNewsService();

    @Test
    void fetchNewsReturnsEmptyListForNullSymbol() {
        assertDoesNotThrow(() -> {
            List<StockNewsService.NewsItem> result = newsService.fetchNews(null);
            assertTrue(result.isEmpty(), "Should return empty list for null symbol");
        });
    }

    @Test
    void fetchNewsReturnsEmptyListForBlankSymbol() {
        assertDoesNotThrow(() -> {
            List<StockNewsService.NewsItem> result = newsService.fetchNews("   ");
            assertTrue(result.isEmpty(), "Should return empty list for blank symbol");
        });
    }

    @Test
    void fetchNewsReturnsValidNewsForValidSymbol() {
        assertDoesNotThrow(() -> {
            List<StockNewsService.NewsItem> result = newsService.fetchNews("AAPL");
            assertNotNull(result, "Result should not be null");
            // Note: AAPL is a valid symbol and should have news available
            // The result may be empty if API is unavailable, but should not throw
        });
    }

    @Test
    void newsItemContainsExpectedFields() {
        assertDoesNotThrow(() -> {
            List<StockNewsService.NewsItem> result = newsService.fetchNews("AAPL");
            for (StockNewsService.NewsItem item : result) {
                assertNotNull(item.getTitle(), "News item should have a title");
                assertNotNull(item.getSource(), "News item should have a source");
                assertTrue(item.getPublishedAt() >= 0, "Published date should be non-negative");
            }
        });
    }

    @Test
    void fetchNewsHandlesInvalidSymbol() {
        assertDoesNotThrow(() -> {
            List<StockNewsService.NewsItem> result = newsService.fetchNews("INVALID_SYMBOL_XYZ");
            assertNotNull(result, "Should handle invalid symbol gracefully");
        });
    }

    @Test
    void fetchNewsReturnsAtMostFiveArticles() {
        assertDoesNotThrow(() -> {
            List<StockNewsService.NewsItem> result = newsService.fetchNews("AAPL");
            assertTrue(result.size() <= 5, "Should return at most 5 articles");
        });
    }

    @Test
    void newsItemFormattedStringIsValid() {
        StockNewsService.NewsItem item = new StockNewsService.NewsItem(
            "Test News Title",
            "Test Source",
            1234567890L,
            "https://example.com"
        );
        String formatted = item.getFormattedString();
        assertNotNull(formatted, "Formatted string should not be null");
        assertTrue(formatted.contains("Test News Title"), "Should contain title");
        assertTrue(formatted.contains("Test Source"), "Should contain source");
    }
}
