package stock.tracker;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class StockQuoteService {
    private static final String YAHOO_CHART_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private static final String YAHOO_SEARCH_URL = "https://query1.finance.yahoo.com/v1/finance/search?q=";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());
    private final HttpClient client = HttpClient.newHttpClient();

    public Map<String, StockItem> fetchQuotes(Collection<String> symbols, Map<String, String> symbolGroup) throws IOException, InterruptedException {
        if (symbols.isEmpty()) {
            return Map.of();
        }

        Map<String, StockItem> quotes = new LinkedHashMap<>();
        Set<String> missing = new LinkedHashSet<>();

        for (String rawSymbol : symbols) {
            if (rawSymbol == null) {
                continue;
            }
            String symbol = rawSymbol.trim().toUpperCase(Locale.US);
            if (symbol.isEmpty()) {
                continue;
            }
            StockItem quote = fetchQuoteForSymbol(symbol, symbolGroup);
            if (quote != null) {
                quotes.put(symbol, quote);
            } else {
                missing.add(symbol);
            }
        }

        if (!missing.isEmpty()) {
            Map<String, StockItem> fallbackQuotes = fetchQuotesFromStooq(missing, symbolGroup);
            fallbackQuotes.forEach(quotes::putIfAbsent);
        }

        for (String rawSymbol : symbols) {
            if (rawSymbol == null) {
                continue;
            }
            String symbol = rawSymbol.trim().toUpperCase(Locale.US);
            if (symbol.isEmpty()) {
                continue;
            }
            quotes.computeIfAbsent(symbol, s -> createUnavailableQuote(s, symbolGroup));
        }

        return quotes;
    }

    private StockItem createUnavailableQuote(String symbol, Map<String, String> symbolGroup) {
        String group = symbolGroup.getOrDefault(symbol, "Ungrouped");
        return new StockItem(symbol, group, "Ticker data unavailable", Double.NaN, Double.NaN, Double.NaN, "Unavailable");
    }

    private StockItem fetchQuoteForSymbol(String symbol, Map<String, String> symbolGroup) throws IOException, InterruptedException {
        StockItem quote = fetchQuoteFromYahooChart(symbol, symbolGroup);
        if (quote != null) {
            return quote;
        }

        String resolvedSymbol = resolveSymbol(symbol);
        if (resolvedSymbol != null && !resolvedSymbol.equalsIgnoreCase(symbol)) {
            StockItem resolvedQuote = fetchQuoteFromYahooChart(resolvedSymbol, symbolGroup);
            if (resolvedQuote != null) {
                return new StockItem(symbol,
                        symbolGroup.getOrDefault(symbol, "Ungrouped"),
                        resolvedQuote.shortName(),
                        resolvedQuote.price(),
                        resolvedQuote.change(),
                        resolvedQuote.changePercent(),
                        resolvedQuote.updatedAt());
            }
        }

        return null;
    }

    private StockItem fetchQuoteFromYahooChart(String symbol, Map<String, String> symbolGroup) throws IOException, InterruptedException {
        String url = YAHOO_CHART_URL + URLEncoder.encode(symbol, StandardCharsets.UTF_8) + "?interval=2m&range=1d";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Java Stock Tracker/1.0")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        return parseYahooChartResponse(response.body(), symbol, symbolGroup);
    }

    private String resolveSymbol(String symbol) throws IOException, InterruptedException {
        String url = YAHOO_SEARCH_URL + URLEncoder.encode(symbol, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Java Stock Tracker/1.0")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        JSONObject json = new JSONObject(response.body());
        JSONArray quotes = json.optJSONArray("quotes");
        if (quotes == null || quotes.isEmpty()) {
            return null;
        }

        String firstEquity = null;
        for (int i = 0; i < quotes.length(); i++) {
            JSONObject item = quotes.getJSONObject(i);
            String quoteType = item.optString("quoteType", "");
            if (!"EQUITY".equalsIgnoreCase(quoteType)) {
                continue;
            }
            String candidate = item.optString("symbol", "");
            if (candidate.isBlank()) {
                continue;
            }
            if (candidate.equalsIgnoreCase(symbol)) {
                return candidate;
            }
            if (firstEquity == null) {
                firstEquity = candidate;
            }
            String exchange = item.optString("exchange", "");
            String exchDisp = item.optString("exchDisp", "");
            if (exchange.toUpperCase(Locale.US).contains("NAS") || exchDisp.toUpperCase(Locale.US).contains("NASDAQ")) {
                return candidate;
            }
        }
        return firstEquity;
    }

    public List<String> fetchTickerSuggestions(String query) throws IOException, InterruptedException {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String url = YAHOO_SEARCH_URL + URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Java Stock Tracker/1.0")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return List.of();
        }

        JSONObject json = new JSONObject(response.body());
        JSONArray quotes = json.optJSONArray("quotes");
        if (quotes == null || quotes.isEmpty()) {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();
        for (int i = 0; i < quotes.length() && suggestions.size() < 8; i++) {
            JSONObject item = quotes.getJSONObject(i);
            String quoteType = item.optString("quoteType", "");
            if (!"EQUITY".equalsIgnoreCase(quoteType)) {
                continue;
            }
            String candidate = item.optString("symbol", "");
            if (candidate.isBlank()) {
                continue;
            }
            if (!suggestions.contains(candidate)) {
                suggestions.add(candidate);
            }
        }
        return suggestions;
    }

    private StockItem parseYahooChartResponse(String responseBody, String symbol, Map<String, String> symbolGroup) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONObject chart = json.optJSONObject("chart");
            if (chart == null) {
                return null;
            }
            JSONArray results = chart.optJSONArray("result");
            if (results == null || results.isEmpty()) {
                return null;
            }
            JSONObject result = results.getJSONObject(0);
            JSONObject meta = result.optJSONObject("meta");
            if (meta == null) {
                return null;
            }
            double price = meta.optDouble("regularMarketPrice", Double.NaN);
            if (!Double.isFinite(price)) {
                return null;
            }
            double previousClose = meta.optDouble("previousClose", Double.NaN);
            if (!Double.isFinite(previousClose)) {
                previousClose = meta.optDouble("chartPreviousClose", Double.NaN);
            }
            double change = Double.NaN;
            double changePercent = Double.NaN;
            if (Double.isFinite(previousClose) && previousClose != 0) {
                change = price - previousClose;
                changePercent = (change / previousClose) * 100;
            }
            String shortName = meta.optString("shortName", symbol);
            String group = symbolGroup.getOrDefault(symbol, "Ungrouped");
            String timestamp = TIME_FORMATTER.format(Instant.now());
            return new StockItem(symbol, group, shortName, price, change, changePercent, timestamp);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, StockItem> fetchQuotesFromStooq(Collection<String> symbols, Map<String, String> symbolGroup) throws IOException, InterruptedException {
        Map<String, StockItem> quotes = new LinkedHashMap<>();
        for (String rawSymbol : symbols) {
            if (rawSymbol == null) {
                continue;
            }
            String symbol = rawSymbol.trim().toUpperCase(Locale.US);
            if (symbol.isEmpty()) {
                continue;
            }
            StockItem quote = fetchQuoteFromStooq(symbol, symbolGroup);
            if (quote != null) {
                quotes.put(symbol, quote);
            }
        }
        return quotes;
    }

    private StockItem fetchQuoteFromStooq(String symbol, Map<String, String> symbolGroup) throws IOException, InterruptedException {
        String querySymbol = symbol.contains(".") ? symbol.toLowerCase(Locale.US) : symbol.toLowerCase(Locale.US) + ".us";
        String url = "https://stooq.com/q/l/?s=" + URLEncoder.encode(querySymbol, StandardCharsets.UTF_8) + "&f=sd2t2ohlcv&h&e=csv";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Java Stock Tracker/1.0")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch quotes from fallback: HTTP " + response.statusCode());
        }
        Map<String, StockItem> parsed = parseStooqCsv(response.body(), symbolGroup);
        return parsed.values().stream().findFirst().orElse(null);
    }

    private Map<String, StockItem> parseStooqCsv(String responseBody, Map<String, String> symbolGroup) {
        String timestamp = TIME_FORMATTER.format(Instant.now());
        String[] lines = responseBody.split("\\r?\\n");
        Map<String, StockItem> quotes = new LinkedHashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] values = line.split(",");
            if (values.length < 8) {
                continue;
            }
            String rawSymbol = values[0].trim();
            if (rawSymbol.isEmpty()) {
                continue;
            }
            String normalizedSymbol = rawSymbol.replaceAll("(?i)\\.us$", "").toUpperCase(Locale.US);
            String closeText = values[6].trim();
            if (closeText.isEmpty() || "N/D".equals(closeText)) {
                continue;
            }
            double close = parseDoubleOrNaN(closeText);
            double open = parseDoubleOrNaN(values[3].trim());
            double change = Double.isFinite(open) ? close - open : Double.NaN;
            double changePercent = Double.isFinite(open) && open != 0 ? (change / open) * 100 : Double.NaN;
            String group = symbolGroup.getOrDefault(normalizedSymbol, symbolGroup.getOrDefault(rawSymbol, "Ungrouped"));
            quotes.put(normalizedSymbol, new StockItem(normalizedSymbol, group, normalizedSymbol, close, change, changePercent, timestamp));
        }
        return quotes;
    }

    private static double parseDoubleOrNaN(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
