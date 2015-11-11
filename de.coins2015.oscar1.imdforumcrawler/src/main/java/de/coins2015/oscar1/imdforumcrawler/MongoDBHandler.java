package de.coins2015.oscar1.imdforumcrawler;

import java.util.Arrays;

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

public class MongoDBHandler {

    private MongoClient mongoClient;
    private MongoCredential mongoCredential;
    private DB db;

    public MongoDBHandler(String host, String port, String userName,
	    String database, String password) {
	this.mongoCredential = MongoCredential.createCredential(userName,
		database, password.toCharArray());

	this.mongoClient = new MongoClient(new ServerAddress(host),
		Arrays.asList(mongoCredential));
	this.db = mongoClient.getDB(database);
    }

    public void storeData(JSONObject data, String collection) {
	DBObject dbObject = (DBObject) JSON.parse(data.toJSONString());
	DBCollection dbCollection = db.getCollection(collection);

	String id = (String) data.get("_id");
	BasicDBObject query = new BasicDBObject("_id", id);
	DBCursor cursor = dbCollection.find(query);

	if (cursor.size() == 0) {
	    dbCollection.insert(dbObject);
	} else {

	    dbCollection.update(query, dbObject, true, false);
	}
    }

}
