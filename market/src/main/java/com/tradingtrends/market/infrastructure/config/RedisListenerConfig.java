package com.tradingtrends.market.infrastructure.config;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final ApplicationContext applicationContext;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        // 모든 등록된 MessageListenerAdapter를 패턴과 함께 추가
        messageListenerAdapters().forEach((pattern, adapter) -> {
            if (pattern.equals("krw-*")) {
                // 'krw-*' 패턴은 coinRedisSubscriberListener에 연결
                container.addMessageListener(adapter, new PatternTopic("krw-*"));
            } else if (pattern.equals("*")) {
                // 'krw-*'로 시작하지 않는 모든 패턴은 stockRedisSubscriberListener에 연결
                container.addMessageListener(adapter, new PatternTopic("[^krw-]*"));
            }
        });
        return container;
    }

    /**
     * Map<String, MessageListenerAdapter>를 수동으로 정의하지 않고,
     * Spring에서 모든 MessageListener Bean을 자동으로 수집하는 방식으로 변경:
     * @return
     */
    @Bean
    public Map<String, MessageListenerAdapter> messageListenerAdapters() {
        return applicationContext.getBeansOfType(MessageListener.class)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> {
                    // 'coinRedisSubscriberListener'면 'krw-*' 패턴을 사용
                    if (entry.getKey().equalsIgnoreCase("coinRedisSubscriberListener")) {
                        return "krw-*";
                    }
                    // 'stockRedisSubscriberListener'면 'krw-*'로 시작하지 않는 모든 패턴을 사용
                    else if (entry.getKey().equalsIgnoreCase("stockRedisSubscriberListener")) {
                        return "*";  // 전체 패턴을 잡고 후속 로직에서 필터링
                    }
                    // 그 외의 리스너는 다른 패턴을 설정
                    return "default-*";  // 필요시 기본 패턴 또는 다른 패턴 설정
                },
                entry -> new MessageListenerAdapter(entry.getValue(), "onMessage")
            ));
    }
}
