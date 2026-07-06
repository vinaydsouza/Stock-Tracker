package stock.tracker;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

public class StockNewsService {
    private static final String NEWS_URL = "https://query1.finance.yahoo.com/v10/finance/quoteSummary/";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private final HttpClient client = HttpClient.newHttpClient();

    public List<NewsItem> fetchNews(String symbol) throws IOException, InterruptedException {
        if (symbol == null || symbol.isBlank()) {
            return List.of();
        }

        String encodedSymbol = URLEncoder.encode(symbol.trim().toUpperCase(Locale.US), StandardCharsets.UTF_8);
        String url = NEWS_URL + encodedSymbol + "?modules=news";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "*/*")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return List.of();
            }
            return parseNewsResponse(response.body());
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<NewsItem> parseNewsResponse(String body) {
        List<NewsItem> newsList = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(body);
            JSONObject quoteSummary = root.optJSONObject("quoteSummary");
            if (quoteSummary == null) {
                return newsList;
            }

            JSONArray result = quoteSummary.optJSONArray("result");
            if (result == null || result.length() == 0) {
                return newsList;
            }

            JSONObject resultItem = result.getJSONObject(0);
            JSONObject news = resultItem.optJSONObject("news");
            if (news == null) {
                return newsList;
            }

            JSONArray newsArray = news.optJSONArray("result");
            if (newsArray == null) {
                return newsList;
            }

            for (int i = 0; i < newsArray.length() && i < 5; i++) {
                JSONObject newsItem = newsArray.getJSONObject(i);
                String title = newsItem.optString("title", "");
                String link = newsItem.optString("link", "");
                long publishedAt = newsItem.optLong("pubDate", 0);
                String source = newsItem.optString("source", "");

                if (!title.isBlank()) {
                    newsList.add(new NewsItem(title, source, publishedAt, link));
                }
            }
        } catch (Exception ignored) {
            // Return empty list on parse error
        }
        return newsList;
    }

    public static class NewsItem {
        private final String title;
        private final String source;
        private final long publishedAt;
        private final String link;

        public NewsItem(String title, String source, long publishedAt, String link) {
            this.title = title;
            this.source = source;
            this.publishedAt = publishedAt;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getSource() {
            return source;
        }

        public long getPublishedAt() {
            return publishedAt;
        }

        public String getLink() {
            return link;
        }

        public String getFormattedString() {
            return String.format("• %s (%s)", title, source);
        }
    }
}
