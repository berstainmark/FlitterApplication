package org.flitter.app;

import org.springframework.web.socket.CloseStatus;


interface ConnectionObserver {
    void onConnectionEstablished();

    void onConnectionClosed(CloseStatus status);

    void onTextMessageReceived(String message);

    void onError(Throwable ex);
}
