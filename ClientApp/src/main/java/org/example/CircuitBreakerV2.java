package org.example;

import lombok.Lombok;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;


public class CircuitBreakerV2 {

    private final int retryTimeout;
    private AtomicLong lastFailureTime;
    private boolean isOpen;

    public CircuitBreakerV2(int retryTimeout) {
        this.retryTimeout = retryTimeout;
        this.lastFailureTime = new AtomicLong(0);
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
            if(connection.getResponseCode() >= 500) {
                open();
                System.out.println("|=======>!!!! Server returned status " + connection.getResponseCode() + " !!!!<=======|");
                throw Lombok.sneakyThrow(e);
            }

        }

        return connection;


    }

    public void checkState() throws Exception {

        if(isOpen){
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFailureTime.get() > retryTimeout) {
                System.out.println("Time is over.");
                close();
            }else {
                throw new Exception("Circuit breaker is open. Request denied.");
            }

        }
    }

    public void close() {
        isOpen = false;
        lastFailureTime.set(0);
        System.out.println("Circuit breaker is closed.");
    }

    public void open() {
        isOpen = true;
        lastFailureTime.set(System.currentTimeMillis());
        System.out.println("Circuit breaker is opened.");
    }
}
