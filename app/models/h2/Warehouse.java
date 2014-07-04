package models.h2;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {
    public String name;
    public List<StockItem> stock = new ArrayList<>();

    public String toString() {
        return name;
    }
}
