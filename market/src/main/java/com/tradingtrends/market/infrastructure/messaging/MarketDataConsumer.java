package com.tradingtrends.market.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class MarketDataConsumer {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // Kafka 메시지 수신 및 처리
    @KafkaListener(topics = "upbit-data", groupId = "coin-group")
    public void listener(ConsumerRecord<String, String> data) {
        log.info("Received message: {}", data.toString());

        try {
            // JSON 데이터를 객체로 변환
            Map<String, String> parsedData = objectMapper.readValue(data.value(), Map.class);

            // 특정 필드가 있는지 확인하고 처리
            if (parsedData.containsKey("market")) {
                String marketCode = parsedData.get("market");
                log.info("Market Code: {}", marketCode);

                // 유저 구독 정보를 확인하여 필요한 종목의 데이터만 처리
                if (isMarketSubscribed(marketCode)) {
                    Map<String, Object> marketData = new HashMap<>();
                    marketData.put("tradePrice", parsedData.get("tradePrice"));
                    marketData.put("signedChangePrice", parsedData.get("signedChangePrice"));
                    marketData.put("signedChangeRate", parsedData.get("signedChangeRate"));

                    // Redis Pub/Sub으로 데이터 전송
                    redisTemplate.convertAndSend(marketCode, marketData);
                    log.info("Channel has been successfully published for market: {}", marketCode);
                } else {
                    log.info("No subscribers for market: {}, skipping Redis publish.", marketCode);
                }
            }
        } catch (Exception e) {
            // JSON 역직렬화 실패 처리
            log.error("Failed to deserialize message", e);
        }
    }

    // 주식 데이터 처리
    @KafkaListener(topics = "stock-data", groupId = "stock-group")
    public void stockDataListener(ConsumerRecord<String, String> data) {
        log.info("Received stock data: {}", data.toString());

        try {
            // JSON 데이터를 객체로 변환
            Map<String, String> parsedData = objectMapper.readValue(data.value(), Map.class);

            log.info("Parsed Stock Data: {}", parsedData);

            // 주식 데이터 처리 로직
            if (parsedData.containsKey("stockCode")) {
                String stockCode = parsedData.get("stockCode");
                log.info("Stock Code: {}", stockCode);

                Map<String, Object> stockData = new HashMap<>();
                stockData.put("currentPrice", parsedData.get("currentPrice"));
                stockData.put("priceChange", parsedData.get("priceChange"));
                stockData.put("changeRate", parsedData.get("changeRate"));

                redisTemplate.convertAndSend(stockCode, stockData);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize stock data", e);
        }
    }

    private boolean isMarketSubscribed(String marketCode) {
        // Redis에서 해당 종목에 구독된 유저가 있는지 확인하는 로직
        Boolean hasSubscribers = redisTemplate.hasKey("market:" + marketCode + ":subscribers");
        return Boolean.TRUE.equals(hasSubscribers);
    }
}
