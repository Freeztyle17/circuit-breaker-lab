package org.example;

import lombok.Lombok;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;


public class CircuitBreaker {

    private final int failureThreshold;
    private final int retryTimeout;
    private int failures;
    private boolean isOpen;

    public CircuitBreaker(int failureThreshold, int retryTimeout) {
        this.failureThreshold = failureThreshold;
        this.retryTimeout = retryTimeout;
        this.failures = 0;
        this.isOpen = false;
    }

    public HttpURLConnection allowRequest(URL url) throws IOException {

        StringBuffer response = new StringBuffer();
        HttpURLConnection connection = null;


        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("|------------------------|");
            System.out.println("|>"+response+"<|");

            in.close();

        } catch (IOException e) {
            System.out.println("|=======>!!!! Server returned status "+connection.getResponseCode()+" !!!!<=======|");
            failures++;
            throw Lombok.sneakyThrow(e);
        }

        return connection;


    }

    public void checkState(){

        if(failures >= failureThreshold){
            open();
        }

        if(isOpen){
            try {
                System.out.println("Circuit breaker is opened. Wait "+retryTimeout/1000+" seconds.");
                Thread.sleep(retryTimeout);
                close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        failures = 0;
        isOpen = false;
        System.out.println("Circuit breaker is closed.");
    }

    public void open() {
        isOpen = true;
    }
}