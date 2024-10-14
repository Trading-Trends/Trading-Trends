package com.tradingtrends.corporate.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorporateMajorFinanceViewCountService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String VIEW_COUNT_KEY = "corp_code_view_count";

    // 조회수 증가
    public void incrementViewCount(String corpCode) {
        redisTemplate.opsForHash().increment(VIEW_COUNT_KEY, corpCode, 1);
    }

    // 조회수가 높은 상위 N개의 corp_code 반환
    public List<String> getTopNCorpCodes(int topN) {
        Map<Object, Object> viewCounts = redisTemplate.opsForHash().entries(VIEW_COUNT_KEY);

        // 조회수 순으로 정렬하여 상위 N개의 corp_code를 반환
        return viewCounts.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare((Long) entry2.getValue(), (Long) entry1.getValue()))
                .limit(topN)
                .map(entry -> (String) entry.getKey())
                .collect(Collectors.toList());
    }
}
