package com.appdynamics.sample.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.regions.Regions;

public class BaseService {

    protected void queryDB(String dbName, String query, String speed, String traffic) {
        MySQLConnection mySQLConnection = new MySQLConnection();
        mySQLConnection.queryDB(dbName, query, speed, traffic);
    }

    protected void processCustomerRequest(String speed, String traffic) {
        processCustomerRequest(speed, traffic, 1);
    }

    protected void processCustomerRequest(String speed, String traffic, int multiplier) {

        String disableThreadContention = System.getenv("DISABLE_THREAD_CONTENTION");

        if (disableThreadContention == null || disableThreadContention.equals("1") == false) {

            int delayInt = 50;
            Random rand = new Random();

            if (speed == null || speed.equals("")) {
                speed="normal";
            }
            if (traffic == null || traffic.equals("")) {
                traffic="light";
            }

            if (speed.equalsIgnoreCase("slow")) {
                delayInt = 1500;
            }
            else if (speed.equalsIgnoreCase("veryslow")) {
                delayInt = 3000;
            }

            if (traffic.equalsIgnoreCase("moderate")) {
                delayInt += rand.nextInt(4000);
            }
            else if (traffic.equalsIgnoreCase("heavy")) {
                delayInt += rand.nextInt(7000);
            }
            else {
                delayInt += rand.nextInt(100);
            }

            BackgroundWorker worker = new BackgroundWorker();
    		worker.doBackgroundWork(delayInt * multiplier);
        }
    }

    protected Response makeWebRequest(String hostName, String port, String callName, String speed, String traffic) {

        try {
            String url = "http://"+hostName+":"+port+"/"+callName+"?speed="+speed+"&traffic="+traffic;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return Response.status(con.getResponseCode()).entity(con.getResponseMessage()).build();
        }
        catch (Exception e) {
            System.out.println("Error from makeWebRequest: " + e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    protected Response makeWebRequest(String hostName, String port, String callName, MultivaluedMap<String,String> queryParams) {
        try {
            List<String> params = new ArrayList<String>();
            for (String str : queryParams.keySet()) {
                params.add(str + "=" + queryParams.getFirst(str));
            }

            String url = "http://"+hostName+":"+port+"/"+callName+"?"+String.join("&", params);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return Response.status(con.getResponseCode()).entity(con.getResponseMessage()).build();
        } catch (Exception e) {
            System.out.println("Error from makeWebRequest: " + e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    protected Response postWebRequest(String hostName, String port, String callName, String postData, String speed, String traffic) {

        try {
            String url = "http://"+hostName+":"+port+"/"+callName+"?speed="+speed+"&traffic="+traffic;
            byte[] postDataBytes = postData.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postDataBytes.length;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setDoOutput( true );
            con.setInstanceFollowRedirects( false );
            con.setRequestMethod( "POST" );
            con.setRequestProperty( "Content-Type", "application/json"); 
            con.setRequestProperty( "charset", "utf-8");
            con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            con.setUseCaches( false );
            con.getOutputStream().write(postDataBytes);

            System.out.println("Response Code : " + con.getResponseCode());
            return Response.status(con.getResponseCode()).entity(con.getResponseMessage()).build();
        }
        catch (Exception e) {
            System.out.println("Error from makeWebRequest: " + e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    protected void callS3(String fileName) {
        uploadS3(fileName);
    }

    protected void uploadS3(String uploadFileName) {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY"), System.getenv("AWS_SECRET_KEY"));
        AmazonS3Client s3client = (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(System.getenv("AWS_REGION")).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

        try {
            
            File temp = File.createTempFile("temp", ".file");
            temp.deleteOnExit();

            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            
            for (int i = 0; i < 100; i++) {
                out.write("abcdefghijklmnopqrstuvwxyz1234567890\n");
            }

            out.close();

            LocalDateTime now = LocalDateTime.now();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();

            String bucketName = System.getenv("AWS_S3_BUCKET_NAME");
            String destinationFile = System.getenv("AWS_S3_PATH") + month + "/" + day + "/file_" + System.currentTimeMillis() + "_" + uploadFileName;
            s3client.putObject(new PutObjectRequest(bucketName, destinationFile, temp));

        } catch (java.io.IOException ioEx) {
            System.out.println("Error Message: " + ioEx.getMessage());
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    protected void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    protected void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

    protected void queryDynamoDB(String tableName, String fieldName) {

        try {
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                            .withRegion(System.getenv("AWS_REGION"))
                            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY"), System.getenv("AWS_SECRET_KEY"))))
                            .build();

            DynamoDB dynamoDB = new DynamoDB(client);
            Table table = dynamoDB.getTable(tableName);

            QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression(fieldName + " = :v_id")
                .withValueMap(new ValueMap().withString(":v_id", "1"));

            ItemCollection<QueryOutcome> items = table.query(spec);

            Iterator<Item> iterator = items.iterator();
            Item item = null;
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.toJSONPretty());
            }
        }
        catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
    	BackgroundWorker worker = new BackgroundWorker();
    	System.out.println("Start=" + new Date() );
		worker.doBackgroundWork(5);
		System.out.println("Stop=" + new Date());
    }

}