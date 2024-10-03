package com.tradingtrends.coin.presentation.controller;

import com.tradingtrends.coin.infrastructure.messaging.UpbitWebSocketService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebsocketController {

    private final UpbitWebSocketService upbitWebSocketService;

    // WebSocket 연결 API
    @PostMapping("/connect-websocket")
    public String connectWebSocket() {
        upbitWebSocketService.connectWebSocket();
        return "WebSocket connected.";
    }

    // WebSocket 해제 API
    @PostMapping("/disconnect-websocket")
    public String disconnectWebSocket() throws IOException {
        upbitWebSocketService.disconnectWebSocket();
        return "WebSocket disconnected.";
    }
}
