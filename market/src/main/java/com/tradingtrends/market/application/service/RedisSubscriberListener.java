package com.tradingtrends.market.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriberListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * Redis에서 convertAndSend 함수로 메시지 수신 시 호출
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신된 메시지를 역직렬화
            String messageBody = new String(message.getBody());
            String channel = new String(message.getChannel());  // 수신된 채널

//            log.info("Received message from channel {}: {}", channel, messageBody);

            // JSON 데이터를 Map으로 변환 (필요에 따라 클래스 형태로 변환 가능)
            Map<String, Object> parsedData = objectMapper.readValue(messageBody, Map.class);

            // 클라이언트가 해당 채널(marketCode)을 구독 중인지 확인 후 데이터 전송
            sendDataToSubscribedClients(channel, parsedData);

        } catch (Exception e) {
            log.error("Failed to process message", e);
        }
    }

    /**
     * 종목별 구독 유저에게 실시간 데이터를 전송하는 로직
     * @param marketCode 종목 코드
     * @param message 실시간 데이터
     */
    public void sendDataToSubscribedClients(String marketCode, Map<String, Object> message) {
        // 종목을 구독 중인 유저 조회
        Set<Object> subscribedUsersObj = redisTemplate.opsForSet().members("market:" + marketCode + ":subscribers");
        Set<String> subscribedUsers = subscribedUsersObj.stream()
            .map(Object::toString)
            .collect(Collectors.toSet());

        if (subscribedUsers != null && !subscribedUsers.isEmpty()) {
            // 해당 종목을 구독한 유저들에게 데이터 전송
            for (String userId : subscribedUsers) {
                log.info("Sending data to user {} for market {}: {}", userId, marketCode, message);
                // 여기서 WebSocket이나 다른 방식으로 유저에게 실시간 데이터를 전송하는 로직 추가 가능
            }
            log.info("Published to Redis Channel: {}", marketCode);
        } else {
            log.info("No users subscribed to market {}", marketCode);
        }
    }



}
