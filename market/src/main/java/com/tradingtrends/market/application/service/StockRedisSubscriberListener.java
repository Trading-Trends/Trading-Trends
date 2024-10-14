package com.tradingtrends.market.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockRedisSubscriberListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final WebSocketService webSocketService;

    /**
     * CoinREdisSubscriberListener 과 기능이 같기때문에 Adapter 나눌 필요가 없을 듯한데..
     * @param message message must not be {@literal null}.
     * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신된 메시지를 역직렬화
            String messageBody = new String(message.getBody());
            String channel = new String(message.getChannel());  // 수신된 채널

            log.info("Received message from stock channel {}: {}", channel, messageBody);

            // JSON 데이터를 Map으로 변환 (필요에 따라 클래스 형태로 변환 가능)
            Map<String, Object> parsedData = objectMapper.readValue(messageBody, Map.class);

            // 클라이언트가 해당 채널(marketCode)을 구독 중인지 확인 후 데이터 전송
            webSocketService.sendDataToSubscribedClients(channel, parsedData);

        } catch (Exception e) {
            log.error("Failed to process stock data message", e);
        }
    }
}
