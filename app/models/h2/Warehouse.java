package models.h2;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Warehouse extends Model{

    public static Model.Finder<Long, Product> find = new Model.Finder<Long, Product>("secondary", Long.class, Product.class);
    public void save() {
        Ebean.getServer("secondary").save(this);
    }

    @Id
    public Long id;

    public String name;

    @OneToMany
    public List<StockItem> stock = new ArrayList<>();

    @OneToOne
    public Address address;

    @ManyToMany
    public List<Employee> employees;

    public String toString() {
        return name;
    }

}
