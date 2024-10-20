package com.tradingtrends.coin.application.service;

import com.tradingtrends.coin.infrastructure.messaging.CoinTopic;
import jakarta.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaCleanUpService {

    private final AdminClient adminClient;

    public KafkaCleanUpService(@Value("${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);  // 설정 파일에서 가져옴
        this.adminClient = AdminClient.create(config);

        // Shutdown Hook 추가
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered for Kafka cleanup.");
            cleanupKafkaTopic();
        }));
    }
    // Kafka 토픽을 삭제하는 메서드
    @PreDestroy
    public void cleanupKafkaTopic() {
        String topic = CoinTopic.UPBIT_DATA.getTopic();
        log.info("Starting Kafka topic cleanup...");

        try {
            // 현재 토픽이 존재하는지 확인
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (existingTopics.contains(topic)) {
                // 토픽이 존재하면 삭제
                adminClient.deleteTopics(Arrays.asList(topic)).all().get();
                log.info("Kafka topic '{}' deleted successfully.", topic);
            } else {
                log.info("Kafka topic '{}' does not exist. No deletion required.", topic);
            }

        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to delete or recreate Kafka topic: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during Kafka topic cleanup: {}", e.getMessage(), e);
        }
    }


}
