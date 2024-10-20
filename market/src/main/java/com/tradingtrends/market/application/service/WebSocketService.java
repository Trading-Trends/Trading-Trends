package com.tradingtrends.market.application.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public void registerSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
        log.info("Registered WebSocket session for user: {}", userId);
    }

    public void unregisterSession(String userId) {
        userSessions.remove(userId);
        log.info("Unregistered WebSocket session for user: {}", userId);
    }

    // Redis에서 데이터를 수신 후, 유저가 해당 종목을 구독하고 있는 경우 WebSocket을 통해 데이터를 전달
    public void sendDataToSubscribedClients(String marketCode, Map<String, Object> message) {
        Set<String> subscribedUsers = getSubscribedUsers(marketCode);
        log.info("Retrieved subscribed users for market {}: {}", marketCode, subscribedUsers);

        if (subscribedUsers != null && !subscribedUsers.isEmpty()) {
            for (String userId : subscribedUsers) {
                WebSocketSession session = userSessions.get(userId);

                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                        log.info("Sent data to user {} for market {}: {}", userId, marketCode, message);
                    } catch (Exception e) {
                        log.error("Failed to send message to user {}", userId, e);
                    }
                }
            }
        } else {
            log.info("No users subscribed to market {}", marketCode);
        }
    }

    private Set<String> getSubscribedUsers(String marketCode) {
        // Redis에서 해당 종목을 구독 중인 유저 조회
        Set<Object> subscribers = redisTemplate.opsForSet().members("market:" + marketCode + ":subscribers");
        return subscribers.stream().map(Object::toString).collect(Collectors.toSet());
    }
}
