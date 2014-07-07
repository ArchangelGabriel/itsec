package models.h2;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Address extends Model{

    public static Model.Finder<Long, Product> find = new Finder<Long, Product>("secondary", Long.class, Product.class);
    public void save() {
        Ebean.getServer("secondary").save(this);
    }

    @Id
    public Long id;

    @OneToOne(mappedBy = "address")
    public Warehouse warehouse;

    public String street;
    public String number;
    public String postalCode;
    public String city;
    public String country;

}
