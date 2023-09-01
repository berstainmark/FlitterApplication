package org.flitter.app;

import org.flitter.app.service.MarketService;
import org.flitter.app.service.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketConnection {

    private static final int MAX_RECONNECT_ATTEMPTS = 300;
    private final List<ConnectionObserver> observers = new ArrayList<>();
    @Autowired
    private MarketService marketService;
    private WebSocketSession session;
    private Flitter flitter;
    private Environment env;
    private int reconnectAttempts = 0;

    public void setFlitter(Flitter flitter) {
        this.flitter = flitter;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public void registerObserver(ConnectionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ConnectionObserver observer) {
        observers.remove(observer);
    }

    public void notifyConnectionEstablished() {
        for (ConnectionObserver observer : observers) {
            observer.onConnectionEstablished();
        }
    }

    public void notifyConnectionClosed(CloseStatus status) {
        for (ConnectionObserver observer : observers) {
            observer.onConnectionClosed(status);
        }
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(() -> {
                connect();
                reconnectAttempts++;
            }, 10, TimeUnit.SECONDS);
        } else {
            Utils utils = new Utils();
            utils.saveLogs("Error", "HISS", "Reached max reconnect attempts. Not reconnecting.");

        }
    }

    public void notifyTextMessageReceived(String message) {
        for (ConnectionObserver observer : observers) {
            observer.onTextMessageReceived(message);
        }
    }

    public void notifyError(Throwable ex) {
        for (ConnectionObserver observer : observers) {
            observer.onError(ex);
        }


        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(() -> {
                connect();
                reconnectAttempts++;
            }, 10, TimeUnit.SECONDS);
        } else {
            Utils utils = new Utils();
            utils.saveLogs("Error", "HISS", "Reached max reconnect attempts. Not reconnecting.");

        }
    }


    public void start() {
        connect();
        try {
            Thread.sleep(200);
            sendMessage("config", "login");
        } catch (Exception ex) {

        }
    }

    private void connect() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        MyTextWebSocketHandler handler = new MyTextWebSocketHandler();

        URI uri = URI.create(env.getProperty("websocket.url"));
        ListenableFuture<WebSocketSession> future = client.doHandshake(handler, uri.toString());
        future.addCallback(new ListenableFutureCallback<WebSocketSession>() {
            @Override
            public void onFailure(Throwable ex) {
                notifyError(ex);
                Utils utils = new Utils();
                utils.saveLogs("Error", "HISS", "Error failure: " + ex.getMessage());
            }

            @Override
            public void onSuccess(WebSocketSession session) {
                reconnectAttempts = 0;
                WebSocketConnection.this.session = session;
                notifyConnectionEstablished();
                try {
                    Thread.sleep(200);
                    sendMessage("config", "login");
                } catch (Exception ex) {

                }
            }
        });
    }


    public void sendMessage(String type, String message) throws IOException {
        JSONObject json = new JSONObject();
        json.put("name", "user");
        json.put("type", type);
        json.put("message", message);
        json.put("hash", PrivateConfig.HISS_KEY);

        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(json.toString()));
        }
    }

    class MyTextWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            String payload = message.getPayload();

            try {
                JSONObject json = new JSONObject(payload);
                String name = (String) json.get("name");
                String type = (String) json.get("type");
                String text = (String) json.get("message");

                if (name == null || type == null || text == null) {
                    return;
                }

                if ("market".equals(type) && "hiss".equals(name)) {
                    marketService.processMessage(type, text);
                } else if ("ui".equals(type) && "hiss".equals(name)) {
                    notifyTextMessageReceived(message.getPayload());
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            Utils utils = new Utils();
            utils.saveLogs("Info", "HISS", "Connection established");
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            notifyConnectionClosed(status);
            Utils utils = new Utils();
            utils.saveLogs("Info", "HISS", "Connection closed. Status: " + status);
        }
    }


}
