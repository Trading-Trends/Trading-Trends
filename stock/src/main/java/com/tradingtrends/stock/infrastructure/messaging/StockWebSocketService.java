package com.tradingtrends.stock.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingtrends.stock.application.dto.ApprovalRequest;
import com.tradingtrends.stock.infrastructure.StockTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockWebSocketService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private WebSocketSession webSocketSession;

    // WebSocket 연결
    public void connectWebSocket(String approvalKey, String custType, String trType, ApprovalRequest approvalRequest) {
        if (webSocketSession != null && webSocketSession.isOpen()) {
            log.info("WebSocket is already connected.");
            return;
        }

        StandardWebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new StockWebSocketHandler(approvalKey, custType, trType, approvalRequest), "ws://ops.koreainvestment.com:21000");
        log.info("WebSocket connection initiated.");
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

    // WebSocket 핸들러 클래스 정의
    private class StockWebSocketHandler extends TextWebSocketHandler {
        private final String approvalKey;
        private final String custType;
        private final String trType;
        private final ApprovalRequest approvalRequest;

        public StockWebSocketHandler(String approvalKey, String custType, String trType, ApprovalRequest approvalRequest) {
            this.approvalKey = approvalKey;
            this.custType = custType;
            this.trType = trType;
            this.approvalRequest = approvalRequest;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            log.info("WebSocket connected");

            // 웹소켓 세션을 저장
            webSocketSession = session;

            // 코스피100 종목코드 리스트
            String[] stockCodes = {
                    "282330", "097950", "005830", "383220", "078930", "007070", "009540",
                    "267250", "329180", "011200", "105560", "030200", "033780", "003550",
                    "034220", "051900", "373220", "032640", "011070", "066570", "051910",
                    "035420", "005940", "005490", "010950", "034730", "011790", "302440",
                    "326030", "402340", "361610", "096770", "017670", "000660", "035250",
                    "010130", "001570", "011780", "000270", "024110", "251270", "003490",
                    "454910", "241560", "034020", "004990", "011170", "138040", "006800",
                    "028050", "006400", "028260", "207940", "032830", "018260", "009150",
                    "005930", "010140", "016360", "029780", "000810", "068270", "055550",
                    "002790", "090430", "450080", "036570", "066970", "271560", "316140",
                    "000100", "035720", "323410", "377300", "021240", "259960", "022100",
                    "047050", "003670", "086790", "352820", "036460", "071050", "015760",
                    "161390", "047810", "042700", "008930", "128940", "018880", "180640",
                    "009830", "012450", "042660", "000720", "086280", "012330", "004020",
                    "005380", "008770"
            };

            for (String stockCode : stockCodes) {
                approvalRequest.setTr_key(stockCode);

                // 요청 데이터 생성
                String sendData = createRequestData(approvalKey, custType, trType, approvalRequest);

                // WebSocket 서버로 메시지 전송
                session.sendMessage(new TextMessage(sendData));
                log.info("Sent message for stock code {}: {}", stockCode, sendData);
            }
        }

        // 요청 데이터를 JSON 형태로 생성
        private String createRequestData(String approvalKey, String custType, String trType, ApprovalRequest approvalRequest) {
            return "{\n" +
                    "  \"header\": {\n" +
                    "    \"approval_key\": \"" + approvalKey + "\",\n" +
                    "    \"custtype\": \"" + custType + "\",\n" +
                    "    \"tr_type\": \"" + trType + "\",\n" +
                    "    \"content-type\": \"utf-8\"\n" +
                    "  },\n" +
                    "  \"body\": {\n" +
                    "    \"input\": {\n" +
                    "      \"tr_id\": \"" + approvalRequest.getTr_id() + "\",\n" +
                    "      \"tr_key\": \"" + approvalRequest.getTr_key() + "\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }

        // WebSocket 서버에서 메시지 수신 처리
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            log.info("Received message: {}", payload);

            // PINGPONG 또는 SUBSCRIBE SUCCESS 메세지 필터링
            // 해당 메시지를 특별한 처리 없이 로그에 기록만 하고 실제 데이터 처리 로직을 실행하지 않고 반환
            if (payload.contains("PINGPONG") || payload.contains("SUBSCRIBE SUCCESS")) {
                log.info("PINGPONG 또는 SUBSCRIBE SUCCESS 메세지입니다.");
                return;
            }

            // 실시간 데이터 파싱 및 JSON 변환
            String jsonData = parseStockData(payload);

            if (jsonData != null) {
                // 수신된 JSON 데이터를 Kafka로 전송
                try {
                    // Kafka로 메시지 전송 및 파티션과 오프셋 로깅 처리
                    CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(StockTopic.STOCK_DATA.getTopic(), jsonData);

                    // 비동기적으로 Kafka 전송 결과 처리
                    future.whenComplete((result, ex) -> {
                        if (ex == null) {
                            // 메시지 전송 성공, 파티션 및 오프셋 로깅
                            log.info("Message sent successfully to partition=[{}], offset=[{}]",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            // 메시지 전송 실패
                            log.error("Message failed to send due to: {}", ex.getMessage());
                        }
                    });
                } catch (Exception e) {
                    log.error("Failed to send message to Kafka: {}", e.getMessage());
                }
            }
        }

        // 실시간 데이터를 파싱하여 필요한 정보만 추출 후 JSON 형식으로 변환
        private String parseStockData(String rawData) {
            try {
                // "|" 문자로 데이터를 분리
                // 수신된 데이터는 여러 부분으로 구성되어 있으므로 "|"를 기준으로 나눔
                String[] parts = rawData.split("\\|");

                // 분리된 데이터가 예상한 부분 수보다 적으면 잘못된 데이터 형식으로 간주하고 로그 출력
                if (parts.length < 4) {
                    log.error("Invalid data format.");
                    return null;
                }

                // 주식 체결 데이터 추출
                // parts[3]에 체결 데이터가 포함되어 있어서 이 걸 "^"로 다시 분리하여 세부 데이터를 추출
                String[] tradeData = parts[3].split("\\^");

                // 체결 데이터가 충분하지 않을 경우 로그에 에러 메시지를 남기고 종료
                if (tradeData.length < 6) {
                    log.error("Insufficient trade data.");
                    return null;
                }

                // 체결 데이터에서 종목코드, 현재가, 전일 대비 가격, 전일 대비율 추출
                String stockCode = tradeData[0]; // 종목코드
                String currentPrice = tradeData[2]; // 현재가
                String priceChange = tradeData[4]; // 전일 대비 가격
                String changeRate = tradeData[5]; // 전일 대비율

                // 추출된 데이터 로그
                log.info("종목코드: {}, 현재가: {}, 전일 대비 가격: {}, 전일 대비율: {}%",
                        stockCode, currentPrice, priceChange, changeRate);

                // JSON 형식으로 변환할 Map 생성
                Map<String, Object> stockDataMap = new HashMap<>();
                stockDataMap.put("stockCode", stockCode);
                stockDataMap.put("currentPrice", currentPrice);
                stockDataMap.put("priceChange", priceChange);
                stockDataMap.put("changeRate", changeRate);

                // Map을 JSON 문자열로 변환
                return objectMapper.writeValueAsString(stockDataMap);
            } catch (Exception e) {
                // 파싱 중 오류
                log.error("Error parsing stock data: {}", e.getMessage());
                return null;
            }
        }

        // WebSocket 연결 종료 처리
        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            log.info("WebSocket connection closed: sessionId={}, status={}", session.getId(), status);
            webSocketSession = null;
        }
    }
}
