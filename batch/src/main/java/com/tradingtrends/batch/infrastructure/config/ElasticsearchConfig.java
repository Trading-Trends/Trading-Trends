package com.tradingtrends.batch.infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CreateRequest;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.io.StringReader;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig{

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    // application.yml에서 설정된 값들을 가져오기
    @Value("${elasticsearch.uris}")
    private String elasticsearchUri;

    @Value("${elasticsearch.username}")
    private String elasticsearchUsername;

    @Value("${elasticsearch.password}")
    private String elasticsearchPassword;

    @Lazy
    private final ElasticsearchClient elasticsearchClient;

    /**
     * ElasticsearchClient 빈을 생성하여 Spring 컨텍스트에 등록
     * @return ElasticsearchClient
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 기본 자격 증명 공급자 설정 (사용자 이름, 비밀번호)
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));

        // Elasticsearch 연결을 위한 RestClientBuilder 설정
        RestClientBuilder builder = RestClient.builder(HttpHost.create(elasticsearchUri))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        // RestClient 및 Transport 생성
        RestClient restClient = builder.build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // ElasticsearchClient 생성
        return new ElasticsearchClient(transport);
    }

    /**
     * 애플리케이션 시작 시 Elasticsearch 인덱스 설정
     * 인덱스가 없으면 nori 분석기 설정과 함께 새로운 인덱스를 생성
     */
    @PostConstruct
    public void configureElasticsearchIndex() throws IOException {

        boolean indexExists = elasticsearchClient.indices().exists(e -> e.index("disclosures")).value();

        if (!indexExists) {
            createDisclosureIndex(elasticsearchClient);
        }
    }

    /**
     * "disclosures" 인덱스를 생성하며, nori 분석기 적용
     * @param client ElasticsearchClient
     * @throws IOException
     */
    private void createDisclosureIndex(ElasticsearchClient client) throws IOException {
        String indexSettings = """
            {
              "settings": {
                "analysis": {
                  "analyzer": {
                    "nori_analyzer": {
                      "type": "custom",
                      "tokenizer": "nori_tokenizer",
                      "filter": ["lowercase"]
                    }
                  }
                }
              },
              "mappings": {
                "properties": {
                  "corpName": {
                    "type": "text",
                    "analyzer": "nori_analyzer"
                  },
                  "reportNm": {
                    "type": "text",
                    "analyzer": "nori_analyzer"
                  },
                  "rceptDt": {
                    "type": "date",
                    "format": "yyyyMMdd"
                  }
                }
              }
            }
            """;

        // 인덱스 생성 요청 전송
        client.indices().create(c -> c
                .index("disclosures")
                .withJson(new StringReader(indexSettings))
        );

        logger.info("Successfully created 'disclosures' index with Nori analyzer.");
    }
}
