package com.tradingtrends.market.infrastructure.config;

import com.tradingtrends.market.application.service.RedisSubscriberListener;
import io.lettuce.core.ReadFrom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    protected LettuceConnectionFactory redisConnectionFactory() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .readFrom(ReadFrom.REPLICA_PREFERRED)
            .build();

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
            .master(redisProperties.getSentinel().getMaster());

        redisProperties.getSentinel().getNodes().forEach(s -> sentinelConfig.sentinel(s.split(":")[0],Integer.valueOf(s.split(":")[1])));
//        sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return new LettuceConnectionFactory(sentinelConfig, clientConfig);
    }

    /**
     * RedisTemplate 을 통해 Redis와 상호작용
     * LettuceConnectionFactory**가 RedisConnectionFactory 인터페이스를 구현한 것
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer()); // Key를 String으로 직렬화
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // Value를 JSON 형태로 직렬화

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

//    /**
//     * Redis 에서 수신된 메시지를 처리하기 위한 MessageListenerAdapter 설정
//     */
////    @Bean
//    public MessageListenerAdapter messageListener(RedisSubscriberListener redisSubscriberListener){
//        return new MessageListenerAdapter(redisSubscriberListener, "onMessage");
//    }

    /**
     * Redis pub/sub 메시지 처리 Listener
     * RedisMessageListenerContainer : 메시지 리스너에 대한 비동기 동작을 제공하는 컨테이너
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                       RedisSubscriberListener redisSubscriberListener){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        // 각 marketCode 가 Redis 의 Pub/Sub "topic" 역할을 함
        // RedisSubscriberListener 를 직접 RedisMessageListenerContainer 에 등록
//        container.addMessageListener(redisSubscriberListener, new PatternTopic("*"));
        return container;
    }

}