package models.mysql;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by Admin on 7/7/14.
 */

@Entity
public class Comment extends Model {

    @Id
    public Long id;

    @OneToOne
    public User user;


    public String commentText;
}
