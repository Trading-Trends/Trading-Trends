package com.tradingtrends.coin.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingtrends.coin.infrastructure.CoinTopic;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpbitWebSocketService {
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 웹소켓 클라이언트 초기화 및 업비트 서버로 연결 시작
    @PostConstruct
    public void connectWebSocket() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new UpbitWebSocketHandler(), "wss://api.upbit.com/websocket/v1", new WebSocketHttpHeaders());
    }

    // 웹소켓 핸들러 클래스 정의
    private class UpbitWebSocketHandler extends BinaryWebSocketHandler {

        // 웹소켓 연결 성공 후 호출되는 메서드
        @Override
        public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
            log.info("WebSocket connected");

            // 웹소켓 연결 후 업비트 서버에 요청 보내기
            String ticketValue = UUID.randomUUID().toString();
            String message = "[{\"ticket\":\"" + ticketValue + "\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]},{\"format\":\"DEFAULT\"}]";

            session.sendMessage(new TextMessage(message));
        }

        // 웹소켓에서 바이너리 메세지를 수신할 때 호출되는 메서드
        @Override
        protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
            String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8); // 수신된 바이너리 데이터를 UTF-8로 변환하여 문자열로 처리
            Map<String, Object> parsedData = parseWebSocketMessage(payload); // 메시지 파싱하여 필요한 데이터 추출

            // 추출된 데이터를 Kafka 토픽으로 전송
            kafkaTemplate.send(CoinTopic.UPBIT_DATA.getTopic(), parsedData)
                    .thenAccept(result -> {
                        log.info("Message sent successfully: {}", parsedData); // 메세지 전송 성공 로그
                    })
                    .exceptionally(ex -> {
                        log.error("Message failed to send: {}", ex.getMessage()); // 메세지 전송 실패 로그
                        return null;
                    });
            log.info("Received binary message converted to text: {}", payload); // 변환된 메세지 로그
        }

        // 웹소켓에서 받은 메세지 데이터를 파싱 후 필요한 필드 추출하는 메서드
        private Map<String, Object> parseWebSocketMessage(String payload) {
            try {
                Map<String, Object> result = objectMapper.readValue(payload, Map.class); // JSON 데이터를 파싱하여 필요한 필드 추출

                Map<String, Object> parsedData = new HashMap<>();
                parsedData.put("market", result.get("code")); // 마켓 코드 (KRW-BTC 등)
                parsedData.put("tradePrice", result.get("trade_price")); // 현재가
                parsedData.put("signedChangePrice", result.get("signed_change_price")); // 전일 대비 값
                parsedData.put("signedChangeRate", result.get("signed_change_rate")); // 전일 대비 등락률

                return parsedData; // 필요한 필드만 추출하여 반환
            } catch (Exception e) {
                log.error("Failed to parse WebSocket message: {}", e.getMessage(), e);
                return new HashMap<>();
            }
        }

        // 웹소켓 통신 오류
        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            log.error("WebSocket error: {}", exception.getMessage());
        }

        // 웹소켓 연결 종료
        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            log.info("WebSocket closed: {}", status);
        }
    }
}

