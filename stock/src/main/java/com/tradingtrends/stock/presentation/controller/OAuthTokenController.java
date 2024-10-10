package com.tradingtrends.stock.presentation.controller;

import com.tradingtrends.stock.application.service.StockOAuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
public class OAuthTokenController {
    @Value("${stock-api.appkey}")
    private String appKey;

    @Value("${stock-api.appsecret}")
    private String appSecret;

    private final StockOAuthTokenService stockOAuthTokenService;

    @GetMapping("/token")
    public String getToken() {
        return stockOAuthTokenService.getAccessToken(appKey, appSecret);
    }
}
