package com.tradingtrends.batch.infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import io.micrometer.common.lang.NonNull;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchHost;

    @Bean
    public RestClient restClient() {
        return RestClient.builder(
                        HttpHost.create(elasticsearchHost)
                )
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(5000)    // 연결 타임아웃: 5000ms
                        .setSocketTimeout(60000)    // 소켓 타임아웃: 60000ms (1분)
                        .setConnectionRequestTimeout(0) // 연결 요청 타임아웃: 무제한 대기 (기본값)
                )
                .build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}

