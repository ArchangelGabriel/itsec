package models;

import java.util.*;

import models.mysql.User;
import play.modules.mongodb.jackson.MongoDB;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample {

    @Id
    @ObjectId
    public String id;

    private static JacksonDBCollection<Sample, String> coll = MongoDB.getCollection("sample", Sample.class, String.class);

    public Sample() {
    }

    public static List<Sample> all() {
        return Sample.coll.find().toArray();
    }

    public static void create(Sample sample) {
        Sample.coll.save(sample);
    }
}

