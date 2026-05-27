package stock.tracker;

public record StockItem(
        String symbol,
        String group,
        String shortName,
        double price,
        double change,
        double changePercent,
        String updatedAt
) {
}
