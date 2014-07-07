package models.h2;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class StockItem extends Model{

    public void save() {
        Ebean.getServer("secondary").save(this);
    }

    @Id
    public Long id;

    @ManyToOne
    public Warehouse warehouse;

    @ManyToOne
    public Product product;

    public Long quantity;

    public static Finder<Long, StockItem> find() {
        return new Finder<Long, StockItem>("secondary", Long.class, StockItem.class);
    }

    public String toString() {
        return product.name + " - " + quantity;
    }
}
