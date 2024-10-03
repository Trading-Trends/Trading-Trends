package com.tradingtrends.coin.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingtrends.coin.infrastructure.CoinTopic;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpbitWebSocketService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private WebSocketSession webSocketSession;

    // 웹소켓 클라이언트 초기화 및 업비트 서버로 연결 시작
    //@PostConstruct // 빈 초기화 후 의존성 주입
    public void connectWebSocket() {
        if (webSocketSession != null && webSocketSession.isOpen()) {
            log.info("WebSocket is already connected.");
            return;
        }
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new UpbitWebSocketHandler(), "wss://api.upbit.com/websocket/v1");
        log.info("Upbit WebSocket connection initiated.");
    }

    // WebSocket 연결 해제
    public void disconnectWebSocket() throws IOException {
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close();
                log.info("WebSocket connection closed.");
            } catch (Exception e) {
                log.error("Failed to close WebSocket: {}", e.getMessage());
            }
        } else {
            log.info("No active WebSocket connection to close.");
        }
    }

    // 웹소켓 핸들러 클래스 정
    private class UpbitWebSocketHandler extends BinaryWebSocketHandler {

        // 웹소켓 연결 성공 후 호출되는 메서드
        @Override
        public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
            log.info("WebSocket connected");

            // 웹소켓 세션을 저장
            webSocketSession = session;

            // 종목 코드 리스트
            List<String> marketCodes = getMarketCodes();

            if (marketCodes.isEmpty()) {
                log.error("종목 코드를 찾을 수 없습니다.");
                session.close(CloseStatus.NORMAL); // 연결 종료
                return;
            }

            // 종목 개수
            log.info("원화 마켓 종목 개수: {}", marketCodes.size());

            // 웹소켓 연결 후 업비트 서버에 요청 보내기
            String ticketValue = UUID.randomUUID().toString();
            String message = "[{\"ticket\":\"" + ticketValue + "\"},{\"type\":\"ticker\",\"codes\":" + marketCodes.toString() + "},{\"format\":\"DEFAULT\"}]";

            session.sendMessage(new TextMessage(message));
        }

        // 웹소켓에서 바이너리 메세지를 수신할 때 호출되는 메서드
        @Override
        protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
            String payload = new String(message.getPayload().array(), StandardCharsets.UTF_8); // 수신된 바이너리 데이터를 UTF-8로 변환하여 문자열로 처리
            Map<String, Object> parsedData = parseWebSocketMessage(payload); // 메시지 파싱하여 필요한 데이터 추출

            try {
                // JSON 직렬화
                String jsonMessage = objectMapper.writeValueAsString(parsedData);

                // Kafka 토픽에 JSON 메시지 전송
                CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(CoinTopic.UPBIT_DATA.getTopic(), jsonMessage);

                // 메시지 전송 결과 처리 (비동기적으로 성공/실패 로직)
                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        // 메시지 전송 성공
                        log.info("Message sent successfully to partition=[{}], offset=[{}]",
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    } else {
                        // 메시지 전송 실패
                        log.error("Message failed to send due to: {}", ex.getMessage());
                        // 필요 시 재시도 로직 추가 가능
                    }
                });
            } catch (JsonProcessingException e) {
                // JSON 직렬화 실패 처리
                log.error("Failed to serialize message", e);
            }
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
            webSocketSession = null; // 세션을 null로 초기화
        }
    }

    // 종목 코드 리스트 api 호출
    private List<String> getMarketCodes() {
        String url = "https://api.upbit.com/v1/market/all";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> marketList = response.getBody();

            // KRW로 시작하는 코드만 필터링
            return marketList.stream()
                .filter(market -> market.get("market").toString().startsWith("KRW"))
                .map(market -> market.get("market").toString())
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to fetch market codes: {}", e.getMessage(), e);
            return List.of(); // 실패 시 빈 리스트 반환
        }
    }
}

