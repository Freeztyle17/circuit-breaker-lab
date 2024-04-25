package org.example;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {
    public static void main(String[] args) throws InterruptedException, MalformedURLException {

        int failureThreshold = 2;
        int retryTimeout = 5000;

        URL url = new URL("http://localhost:8000/hello");

        CircuitBreaker cb = new CircuitBreaker(failureThreshold, retryTimeout);
        CircuitBreakerV2 cbV2 = new CircuitBreakerV2(retryTimeout);
        CircuitBreakerV3 cbV3 = new CircuitBreakerV3(failureThreshold, retryTimeout);

        for(int i=0; i<98; i++){

            try {

                cb.checkState();
                HttpURLConnection req = cb.allowRequest(url);

                System.out.println("|  Server status: " + req.getResponseCode()+"    |");
                System.out.println("|--------Request"+((i+1)>=10 ? (i+1)+"-------|" :  (i+1)+"--------|" ));


            } catch (IOException e) {
                System.out.println(e);
            }

            Thread.sleep(3000);
        }

    }
}
