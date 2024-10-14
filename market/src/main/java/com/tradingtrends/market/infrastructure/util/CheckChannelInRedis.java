package com.tradingtrends.market.infrastructure.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckChannelInRedis {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isMarketSubscribed(String marketCode) {
        // Redis에서 해당 종목에 구독된 유저가 있는지 확인하는 로직
        Boolean hasSubscribers = redisTemplate.hasKey("market:" + marketCode + ":subscribers");
        return Boolean.TRUE.equals(hasSubscribers);
    }
}
