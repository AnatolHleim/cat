package dataBaseController;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Properties;

public class WorkWithMongo {
  private MongoClient mongoClient;
  private MongoCollection<Document> collection;

  public WorkWithMongo(Properties prop) {
    // connect
    mongoClient = new MongoClient(prop.getProperty("hostMongo"), Integer.valueOf(prop.getProperty("portMongo")));
    // select db
    MongoDatabase database = mongoClient.getDatabase(prop.getProperty("db"));
    // get collection
    collection = database.getCollection(prop.getProperty("collection"));
  }

  public void updateByDataDB(String email, String param, String newVal) {
    Bson filter = new Document("email", email);
    Bson newValue = new Document(param, newVal);
    Bson updateOperationDocument = new Document("$set", newValue);
    collection.updateOne(filter, updateOperationDocument);
  }

  public void deleteDb() {
    collection.deleteMany(new Document());
  }
}
