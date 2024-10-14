package com.tradingtrends.market.presentation.controller;

import com.tradingtrends.market.application.service.ClientSubscriptionService;
import com.tradingtrends.market.presentation.request.SubscribeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class ClientSubscriptionController {

    private final ClientSubscriptionService clientSubscriptionService;

    /**
     * 유저가 특정 종목을 구독하는 API
     * @param request
     * @return
     */
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToMarket(@RequestBody SubscribeRequest request) {
        clientSubscriptionService.subscribeUserToMarket(request.getUserId(), request.getMarketCode());
        return ResponseEntity.ok("User " + request.getUserId() + " subscribed to market: " + request.getMarketCode());
    }

    /**
     * 유저가 특정 종목 구독을 취소하는 API
     * @param userId 유저 ID
     * @param marketCode 종목 코드
     * @return ResponseEntity<String>
     */
    @DeleteMapping("/unsubscribe/{userId}/{marketCode}")
    public ResponseEntity<String> unsubscribeFromMarket(@PathVariable String userId, @PathVariable String marketCode) {
        clientSubscriptionService.unsubscribeUserFromMarket(userId, marketCode);
        return ResponseEntity.ok("User " + userId + " unsubscribed from market: " + marketCode);
    }

    /**
     * 종목별 구독 유저에게 실시간 데이터를 전송하는 API
     * (테스트용으로 실제 데이터를 보내는 것은 Kafka와 Redis에서 자동 처리됨)
     * @param marketCode 종목 코드
     * @param message 데이터 (Map 형태로 전송)
     * @return ResponseEntity<String>
     */
}
