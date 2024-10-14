package com.tradingtrends.market.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingtrends.market.infrastructure.util.CheckChannelInRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinMarketDataConsumer {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CheckChannelInRedis checkChannelInRedis;

    // Kafka 메시지 수신 및 처리
    @KafkaListener(topics = "upbit-data", groupId = "coin-group")
    public void listener(ConsumerRecord<String, String> data) {
        log.info("Received coin message: {}", data.toString());

        try {
            // JSON 데이터를 객체로 변환
            Map<String, String> parsedData = objectMapper.readValue(data.value(), Map.class);

            // 특정 필드가 있는지 확인하고 처리
            if (parsedData.containsKey("market")) {
                String marketCode = parsedData.get("market");
                log.info("Market Code: {}", marketCode);

                // 유저 구독 정보를 확인하여 필요한 종목의 데이터만 처리
                if (checkChannelInRedis.isMarketSubscribed(marketCode)) {
                    Map<String, Object> marketData = new HashMap<>();
                    marketData.put("tradePrice", parsedData.get("tradePrice"));
                    marketData.put("signedChangePrice", parsedData.get("signedChangePrice"));
                    marketData.put("signedChangeRate", parsedData.get("signedChangeRate"));

                    // Redis Pub/Sub으로 데이터 전송
                    redisTemplate.convertAndSend(marketCode, marketData);
                    log.info("Coin Channel has been successfully published for market: {}", marketCode);
                } else {
                    log.info("No subscribers for coin market: {}, skipping Redis publish.", marketCode);
                }
            }
        } catch (Exception e) {
            // JSON 역직렬화 실패 처리
            log.error("Failed to deserialize message", e);
        }
    }

}
