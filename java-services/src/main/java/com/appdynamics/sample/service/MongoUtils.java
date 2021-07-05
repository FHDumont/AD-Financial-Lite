package com.appdynamics.sample.service;

import java.io.*;
import java.util.*;
import java.net.*;

import org.apache.log4j.Logger;
import java.util.Random;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.Random;

public class MongoUtils
{
    Logger logger = Logger.getLogger(MongoUtils.class);
    private String MONGO_URL; 

    public MongoUtils(String mongoURL) {
        MONGO_URL = mongoURL;
    }

    public void insertLoanInfo(String loanId, Document loanInfo, int nextStepDelay) {

        if (nextStepDelay > 0) {

            Date nextStepAfter = new Date(System.currentTimeMillis() + (nextStepDelay * 1000L));
            loanInfo.put("nextStepAfter", nextStepAfter);
            
            MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
            MongoDatabase database = mongoClient.getDatabase("loans");
            MongoCollection<Document> collection = database.getCollection("loans");
            collection.insertOne(loanInfo);
            mongoClient.close();            
        }
    
    }

    public Document getRandomDocument(Document filter) {

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        MongoDatabase database = mongoClient.getDatabase("loans");
        MongoCollection<Document> collection = database.getCollection("loans");
        int recordCount = (int) collection.count(filter);

        if (recordCount > 1) {
    
            Random rand = new Random();
            int selectedIndex = rand.nextInt(recordCount - 1);
            Document result = collection.find(filter).skip(selectedIndex).limit(1).first(); 
            mongoClient.close();
            return result;

        }
        else {

            mongoClient.close();
            return null;

        }
    }

    public void markVerifyDocumentationDate(String loanId, int nextStepDelay) {

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        MongoDatabase database = mongoClient.getDatabase("loans");
        MongoCollection<Document> collection = database.getCollection("loans");

        if (nextStepDelay > 0) {

            Date nextStepAfter = new Date(System.currentTimeMillis() + (nextStepDelay * 1000L));
            collection.findOneAndUpdate(new Document("loanId", loanId), new Document("$set", new Document("verifyDocumentationDate", new Date()).append("nextStepAfter", nextStepAfter)));
            mongoClient.close();

        }
        else {

            collection.deleteOne(new Document("loanId", loanId));
            mongoClient.close();

        }
    }

    public void markCreditCheckDate(String loanId, int nextStepDelay, boolean creditCheckPass, String creditCheckProvider) {

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        MongoDatabase database = mongoClient.getDatabase("loans");
        MongoCollection<Document> collection = database.getCollection("loans");

        if (creditCheckPass && nextStepDelay > 0) {

            Date nextStepAfter = new Date(System.currentTimeMillis() + (nextStepDelay * 1000L));
            collection.findOneAndUpdate(new Document("loanId", loanId), new Document("$set", new Document("creditCheckDate", new Date()).append("nextStepAfter", nextStepAfter)));
            mongoClient.close();

        }
        else {

            collection.deleteOne(new Document("loanId", loanId));
            mongoClient.close();

        }
    }

    public void markLoanUnderwritingCompleteDate(String loanId, int nextStepDelay) {

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        MongoDatabase database = mongoClient.getDatabase("loans");
        MongoCollection<Document> collection = database.getCollection("loans");

        if (nextStepDelay > 0) {

            Date nextStepAfter = new Date(System.currentTimeMillis() + (nextStepDelay * 1000L));
            collection.findOneAndUpdate(new Document("loanId", loanId), new Document("$set", new Document("loanUnderwritingCompleteDate", new Date()).append("nextStepAfter", nextStepAfter)));
            mongoClient.close();

        }
        else {

            collection.deleteOne(new Document("loanId", loanId));
            mongoClient.close();

        }
    }

    public void markLoanApprovedDate(String loanId) {

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        MongoDatabase database = mongoClient.getDatabase("loans");
        MongoCollection<Document> collection = database.getCollection("loans");
        collection.findOneAndUpdate(new Document("loanId", loanId), new Document("$set", new Document("loanApprovedDate", new Date())));
        mongoClient.close();
    }
}
