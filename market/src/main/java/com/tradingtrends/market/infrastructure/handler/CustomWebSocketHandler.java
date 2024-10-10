package com.tradingtrends.market.infrastructure.handler;

import com.tradingtrends.market.application.service.WebSocketService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        webSocketService.registerSession(userId, session);
        userSessions.put(userId, session);
        log.info("User {} connected via WebSocket", userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지 처리 로직 (예: 특정 구독 처리)
        log.info("Message received from {}: {}", session.getId(), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        webSocketService.unregisterSession(userId);
        userSessions.remove(userId);
        log.info("User {} disconnected from WebSocket", userId);
    }
}
