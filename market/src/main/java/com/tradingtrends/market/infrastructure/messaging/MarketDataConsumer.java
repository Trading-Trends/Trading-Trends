package com.tradingtrends.market.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataConsumer {
    @KafkaListener(topics = "upbit-data", groupId = "market-group")
    public void handleCoinEvent(Map<String, Object> message) {
        try {
            log.info("Consumed message: {}", message);
        } catch (Exception e) {
            log.error("Failed to consume message: {}", e.getMessage(), e);
        }
    }
}
