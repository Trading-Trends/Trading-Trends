package com.tradingtrends.batch.infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;

@Service
@RequiredArgsConstructor
public class ElasticsearchInitializer {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void configureElasticsearchIndex() throws IOException {
        boolean indexExists = elasticsearchClient.indices().exists(e -> e.index("disclosures")).value();

        if (!indexExists) {
            createDisclosureIndex(elasticsearchClient);
        }
    }

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

        client.indices().create(c -> c
                .index("disclosures")
                .withJson(new StringReader(indexSettings))
        );

        LoggerFactory.getLogger(ElasticsearchInitializer.class).info("Successfully created 'disclosures' index with Nori analyzer.");
    }
}
