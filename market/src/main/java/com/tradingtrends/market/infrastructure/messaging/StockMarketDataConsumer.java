package com.tradingtrends.market.infrastructure.messaging;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingtrends.market.infrastructure.util.CheckChannelInRedis;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketDataConsumer {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CheckChannelInRedis checkChannelInRedis;

    // 주식 데이터 처리
    @KafkaListener(topics = "stock-data", groupId = "stock-group")
    public void stockDataListener(ConsumerRecord<String, String> data) {
        log.info("Received stock message: {}", data.toString());

        try {
            // JSON 데이터를 객체로 변환
            Map<String, String> parsedData = objectMapper.readValue(data.value(), Map.class);

            // 주식 데이터 처리 로직
            if (parsedData.containsKey("stockCode")) {
                String stockCode = parsedData.get("stockCode");
                log.info("Stock Code: {}", stockCode);

                if(checkChannelInRedis.isMarketSubscribed(stockCode)){
                    Map<String, Object> stockData = new HashMap<>();
                    stockData.put("currentPrice", parsedData.get("currentPrice"));
                    stockData.put("priceChange", parsedData.get("priceChange"));
                    stockData.put("changeRate", parsedData.get("changeRate"));

                    redisTemplate.convertAndSend(stockCode, stockData);
                    log.info("Stock Channel has been successfully published for market: {}", stockCode);

                }else {
                    log.info("No subscribers for stock market: {}, skipping Redis publish.", stockCode);
                }


            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize stock data", e);
        }
    }

}
