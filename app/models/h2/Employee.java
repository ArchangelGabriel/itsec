package models.h2;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Employee extends Model{

    @Id
    public Long id;

    public String firstName;
    public String lastName;

    @ManyToMany(mappedBy = "employees")
    public List<Warehouse> warehouses;
}
