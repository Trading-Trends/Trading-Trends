package com.tradingtrends.market.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataConsumer {

    private final ObjectMapper objectMapper;

    // Kafka 메시지 수신 및 처리
    @KafkaListener(topics = "upbit-data", groupId = "market-group")
    public void listener(ConsumerRecord<String, String> data) {
        log.info("Received message: {}", data.toString());

        try {
            // JSON 데이터를 객체로 변환
            Map<String, String> parsedData = objectMapper.readValue(data.value(), Map.class);

            // 데이터를 처리하거나 필요에 따라 로직 추가
            log.info("Parsed Data: {}", parsedData);

            // 예: 특정 필드가 있는지 확인하고 처리
            if (parsedData.containsKey("market")) {
                String marketCode = (String) parsedData.get("market");
                log.info("Market Code: {}", marketCode);
            }
        } catch (JsonProcessingException e) {
            // JSON 역직렬화 실패 처리
            log.error("Failed to deserialize message", e);
        }
    }
}
