package stock.tracker;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages caching and retrieval of company logos for stock tickers.
 * Logos are fetched from clearbit.com API and cached locally.
 */
public class CompanyLogoCache {
    private static final String CACHE_DIR = System.getProperty("user.home") + "/.stock-tracker/logos";
    private static final int LOGO_SIZE = 32;
    private static final Map<String, String> DOMAIN_MAP = new HashMap<>();
    
    static {
        // Map ticker symbols to company domains
        DOMAIN_MAP.put("AAPL", "apple.com");
        DOMAIN_MAP.put("MSFT", "microsoft.com");
        DOMAIN_MAP.put("GOOGL", "google.com");
        DOMAIN_MAP.put("GOOG", "google.com");
        DOMAIN_MAP.put("META", "meta.com");
        DOMAIN_MAP.put("TSLA", "tesla.com");
        DOMAIN_MAP.put("RIVN", "rivian.com");
        DOMAIN_MAP.put("LCID", "lucidmotors.com");
        DOMAIN_MAP.put("NIO", "nio.com");
        DOMAIN_MAP.put("NVDA", "nvidia.com");
        DOMAIN_MAP.put("AMD", "amd.com");
        DOMAIN_MAP.put("AVGO", "broadcom.com");
        DOMAIN_MAP.put("JPM", "jpmorganchase.com");
        DOMAIN_MAP.put("BAC", "bankofamerica.com");
        DOMAIN_MAP.put("GS", "goldmansachs.com");
        DOMAIN_MAP.put("PFE", "pfizer.com");
        DOMAIN_MAP.put("JNJ", "jnj.com");
        DOMAIN_MAP.put("AZN", "astrazeneca.com");
        DOMAIN_MAP.put("XOM", "exxonmobil.com");
        DOMAIN_MAP.put("CVX", "chevron.com");
        DOMAIN_MAP.put("COP", "conocophillips.com");
        DOMAIN_MAP.put("AMZN", "amazon.com");
        DOMAIN_MAP.put("WMT", "walmart.com");
        DOMAIN_MAP.put("HD", "homedepot.com");
        DOMAIN_MAP.put("NFLX", "netflix.com");
        DOMAIN_MAP.put("DIS", "disney.com");
        DOMAIN_MAP.put("PARA", "paramount.com");
    }
    
    static {
        // Create cache directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(CACHE_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create logo cache directory: " + e.getMessage());
        }
    }
    
    /**
     * Get the logo for a ticker symbol.
     * Returns a cached logo if available, otherwise fetches and caches it.
     *
     * @param symbol The stock ticker symbol
     * @return ImageIcon of the company logo, or null if not available
     */
    public static ImageIcon getLogoForTicker(String symbol) {
        String domain = DOMAIN_MAP.get(symbol.toUpperCase());
        if (domain == null) {
            return null; // No mapping for this symbol
        }
        
        return getLogoForDomain(domain, symbol);
    }
    
    /**
     * Get the logo for a company domain.
     *
     * @param domain The company domain (e.g., apple.com)
     * @param symbol The stock symbol (used for caching)
     * @return ImageIcon of the company logo, or null if not available
     */
    private static ImageIcon getLogoForDomain(String domain, String symbol) {
        try {
            // Check if logo is cached locally
            File cachedFile = new File(CACHE_DIR, symbol + ".png");
            if (cachedFile.exists()) {
                BufferedImage img = ImageIO.read(cachedFile);
                if (img != null) {
                    return new ImageIcon(img.getScaledInstance(LOGO_SIZE, LOGO_SIZE, BufferedImage.SCALE_SMOOTH));
                }
            }
            
            // Fetch logo from clearbit API
            String logoUrl = "https://logo.clearbit.com/" + domain + "?size=" + (LOGO_SIZE * 2);
            BufferedImage img = downloadImage(logoUrl);
            if (img != null) {
                // Cache the image
                ImageIO.write(img, "png", cachedFile);
                return new ImageIcon(img.getScaledInstance(LOGO_SIZE, LOGO_SIZE, BufferedImage.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("Failed to load logo for " + symbol + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Download an image from a URL.
     *
     * @param urlString The URL of the image
     * @return BufferedImage, or null if download fails
     */
    private static BufferedImage downloadImage(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return ImageIO.read(connection.getInputStream());
        } catch (IOException e) {
            System.err.println("Failed to download image from " + urlString + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Clear the local logo cache.
     */
    public static void clearCache() {
        try {
            File dir = new File(CACHE_DIR);
            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    if (file.getName().endsWith(".png")) {
                        Files.delete(file.toPath());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to clear logo cache: " + e.getMessage());
        }
    }
}
