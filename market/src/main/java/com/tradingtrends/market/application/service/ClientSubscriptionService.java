package com.tradingtrends.market.application.service;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientSubscriptionService {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final CoinRedisSubscriberListener coinRedisSubscriberListener;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void restoreSubscriptions() {
        Set<String> subscribedMarkets = redisTemplate.keys("market:*:subscribers");

        if (subscribedMarkets != null) {
            for (String marketKey : subscribedMarkets) {
                String marketCode = marketKey.split(":")[1];  // "market:KRW-BTC:subscribers"에서 marketCode 추출
                redisMessageListenerContainer.addMessageListener(coinRedisSubscriberListener, new ChannelTopic(marketCode));
                log.info("Restored subscription to Redis channel for market: {}", marketCode);
            }
        }
    }

    /**
     * 유저가 특정 종목(marketCode)을 구독할 때 Redis에 저장하는 로직
     * @param userId 유저 ID
     * @param marketCode 관심있는 종목 코드
     * marketCode
     * - coin : KRW-*
     * - stock : ex) 000000
     */
    public void subscribeUserToMarket(Long userId, String marketCode) {
        // 유저가 이미 구독 중인지 확인
        Boolean isAlreadySubscribed = redisTemplate.hasKey("market:" + marketCode + ":subscribers");


        // Redis에서 종목별로 구독한 유저 관리 (유저 ID를 종목 코드에 추가)
        redisTemplate.opsForSet().add("market:" + marketCode + ":subscribers", String.valueOf(userId));
        log.info("User {} subscribed to market {}", String.valueOf(userId), marketCode);

        // 채널(해당 종목)이 아직 구독되지 않았을 경우에만 Redis Pub/Sub 채널 구독을 추가
        if (!Boolean.TRUE.equals(isAlreadySubscribed)) {
            redisMessageListenerContainer.addMessageListener(coinRedisSubscriberListener, new ChannelTopic(marketCode));
            log.info("Subscribed to Redis channel for market: {}", marketCode);
        }
    }

    /**
     * 유저가 종목 구독을 취소할 때 Redis에서 제거하는 로직
     * @param userId 유저 ID
     * @param marketCode 관심있는 종목 코드
     */
    public void unsubscribeUserFromMarket(String userId, String marketCode) {
        // Redis에서 해당 종목에 대해 유저 ID를 제거
        redisTemplate.opsForSet().remove("market:" + marketCode + ":subscribers", userId);
        log.info("User {} unsubscribed from market {}", userId, marketCode);

        // 리스너는 제거하지 않음 - 다른 유저들이 여전히 구독 중일 수 있음
    }

}
