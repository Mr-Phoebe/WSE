package com.wse;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


/**
 * Created by chaoqunhuang on 10/28/17.
 */
public class MongoDb {
    private static MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

    public static void writeToMongoDb(int docId, String url,String content) {
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("web");
        Document doc = new Document("docId", docId)
                .append("url", url)
                .append("content", content);
        collection.insertOne(doc);
    }
}
