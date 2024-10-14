package com.tradingtrends.batch.infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.StringReader;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);
    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void configureElasticsearchIndex() throws IOException {
        // 인덱스가 존재하는지 확인
        boolean indexExists = elasticsearchClient.indices().exists(e -> e.index("disclosures")).value();

        if (!indexExists) {
            // 인덱스가 없다면 새로운 인덱스를 생성하며 Nori 분석기 설정 적용
            createDisclosureIndex();
        }
    }

    private void createDisclosureIndex() throws IOException {
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

        // 인덱스 생성 요청
        elasticsearchClient.indices().create(c -> c
                .index("disclosures")
                .withJson(new StringReader(indexSettings))
        );

        logger.info("Successfully created 'disclosures' index with Nori analyzer.");
    }
}