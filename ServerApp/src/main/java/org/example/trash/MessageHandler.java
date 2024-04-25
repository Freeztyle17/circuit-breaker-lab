package org.example.trash;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
public class MessageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Чтение сообщения от клиента
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String message = br.readLine();

        // Отправка ответа клиенту
        String response = "Server received message: " + message;
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}