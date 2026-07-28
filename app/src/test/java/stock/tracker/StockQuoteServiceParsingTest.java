package stock.tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;

class StockQuoteServiceParsingTest {
    private final StockQuoteService service = new StockQuoteService();

    @Test
    void parseYahooChartUsesPostMarketPriceDuringPostMarket() throws Exception {
        String body = """
                {
                  "chart": {
                    "result": [
                      {
                        "meta": {
                          "shortName": "Apple Inc.",
                          "regularMarketPrice": 190.0,
                          "previousClose": 188.0,
                          "postMarketPrice": 191.5,
                          "postMarketChange": 3.5,
                          "postMarketChangePercent": 1.86,
                          "marketState": "POST"
                        }
                      }
                    ]
                  }
                }
                """;

        StockItem item = invokeParse(body, "AAPL");

        assertNotNull(item);
        assertEquals(191.5, item.price(), 0.0001);
        assertEquals(3.5, item.change(), 0.0001);
        assertEquals(1.86, item.changePercent(), 0.0001);
    }

    @Test
    void parseYahooChartUsesPreMarketPriceDuringPreMarket() throws Exception {
        String body = """
                {
                  "chart": {
                    "result": [
                      {
                        "meta": {
                          "shortName": "Apple Inc.",
                          "regularMarketPrice": 190.0,
                          "previousClose": 188.0,
                          "preMarketPrice": 189.25,
                          "preMarketChange": 1.25,
                          "preMarketChangePercent": 0.66,
                          "marketState": "PRE"
                        }
                      }
                    ]
                  }
                }
                """;

        StockItem item = invokeParse(body, "AAPL");

        assertNotNull(item);
        assertEquals(189.25, item.price(), 0.0001);
        assertEquals(1.25, item.change(), 0.0001);
        assertEquals(0.66, item.changePercent(), 0.0001);
    }

    @Test
    void parseYahooChartFallsBackToExtendedPriceWhenRegularMissing() throws Exception {
        String body = """
                {
                  "chart": {
                    "result": [
                      {
                        "meta": {
                          "shortName": "Apple Inc.",
                          "previousClose": 188.0,
                          "postMarketPrice": 191.5,
                          "marketState": "REGULAR"
                        }
                      }
                    ]
                  }
                }
                """;

        StockItem item = invokeParse(body, "AAPL");

        assertNotNull(item);
        assertEquals(191.5, item.price(), 0.0001);
        assertEquals(3.5, item.change(), 0.0001);
        assertEquals(1.8617, item.changePercent(), 0.0002);
    }

    private StockItem invokeParse(String body, String symbol) throws Exception {
        Method parseMethod = StockQuoteService.class.getDeclaredMethod(
                "parseYahooChartResponse",
                String.class,
                String.class,
                Map.class
        );
        parseMethod.setAccessible(true);
        return (StockItem) parseMethod.invoke(service, body, symbol, Map.of(symbol, "Tech"));
    }
}
