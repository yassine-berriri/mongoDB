package org.example;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Main {
    public static void main(String[] args) {
        MongoClient mongo = MongoClients.create();
        MongoCredential credential;
        credential = MongoCredential.createCredential("sampleUser","MyDB", "password".toCharArray());
        System.out.println("connected to the DB successfully");

        //access to the DB
        MongoDatabase db = mongo.getDatabase("myDB");
        System.out.println("Credentials"+credential);
    }
}