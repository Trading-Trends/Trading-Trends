package com.tradingtrends.stock.application.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class StockWebSocketKeyService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://openapi.koreainvestment.com:9443/oauth2/Approval";

    public String getApprovalKey(String appKey, String appSecret) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("appkey", appKey);
        requestBody.put("secretkey", appSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);
        return (String) response.getBody().get("approval_key");
    }
}