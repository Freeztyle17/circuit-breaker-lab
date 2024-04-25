package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class HelloHandler implements HttpHandler {

    boolean condition = false;

    int serverTrueCondition = 200;
    int serverFalseCondition = 500;

    public HelloHandler() {
        startTimer();
    }

    public boolean checkTime() {
        condition = !condition;
        return condition;
    }
    public void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                condition = checkTime();
                System.out.println("My state is changed: " + (condition ? serverTrueCondition : serverFalseCondition));

            }

        }, 0, 8000);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if(condition){

            String response = "Hello from the server!";
            exchange.sendResponseHeaders(serverTrueCondition, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();

            System.out.println("client connected ^^! ");

            os.write(response.getBytes());
            os.close();

        } else {
            exchange.sendResponseHeaders(serverFalseCondition, 0);
            System.out.println("I'm broken((");
        }

    }

}
