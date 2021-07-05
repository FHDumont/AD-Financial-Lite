package com.appdynamics.sample.service;

import java.util.Random;
import java.io.*;
import java.util.*;
import java.net.*;

public class MySQLConnection
{
    public MySQLConnection() {
    }

    public void queryDB(String dbName, String query, String speed, String traffic) {

        try {
            int delayInt = 50;
            Random rand = new Random();
            delayInt += rand.nextInt(1000);

            try
            {
                Thread.sleep(delayInt);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}