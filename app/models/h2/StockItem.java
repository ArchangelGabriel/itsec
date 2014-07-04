package models.h2;

public class StockItem {
    public Warehouse warehouse;
    public Product product;
    public Long quantity;

    public String toString() {
        return String.format("%d %s", quantity, product);
    }
}
