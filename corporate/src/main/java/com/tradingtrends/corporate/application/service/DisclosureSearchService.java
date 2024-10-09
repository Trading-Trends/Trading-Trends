package com.tradingtrends.corporate.application.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tradingtrends.corporate.domain.model.entity.DisclosureDocument;
import com.tradingtrends.corporate.presentation.request.DisclosureSearchRequestDto;
import com.tradingtrends.corporate.application.dto.DisclosureResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisclosureSearchService {

    private final ElasticsearchClient elasticsearchClient;

    // corpName, reportNm, rceptDt를 사용한 검색 기능 (기간 범위 포함)
    public List<DisclosureResponseDto> searchDisclosure(DisclosureSearchRequestDto requestDto) throws IOException {
        // bool 쿼리에 조건을 추가할 리스트
        List<co.elastic.clients.elasticsearch._types.query_dsl.Query> queries = new ArrayList<>();
        String corpName = requestDto.getCorpName();
        String reportNm = requestDto.getReportNm();
        String startDate = requestDto.getStartDate();
        String endDate = requestDto.getEndDate();

        // null이 아닌 경우에만 조건 추가
        if (corpName != null && !corpName.isEmpty()) {
            queries.add(co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q
                    .match(m -> m.field("corpName").query(corpName))
            ));
        }
        if (reportNm != null && !reportNm.isEmpty()) {
            queries.add(co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q
                    .match(m -> m.field("reportNm").query(reportNm))
            ));
        }

        // rceptDt에 대해 날짜 범위 검색 조건 추가 (startDate와 endDate가 모두 null이 아니면 적용)
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            queries.add(co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q
                    .range(r -> r
                            .date(d -> d
                                    .field("rceptDt")   // 필드 지정
                                    .gte(startDate)     // 시작 날짜
                                    .lte(endDate)       // 종료 날짜
                            )
                    )
            ));
        }

        // 검색 요청을 구성
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("disclosures")
                .query(q -> q
                        .bool(b -> b.must(queries)) // 모든 필터를 적용
                )
                .source(src -> src
                        .filter(f -> f
                                .excludes("rawXmlData")  // rawXmlData를 제외
                        )
                )
        );

        // 검색 실행 및 결과 반환
        SearchResponse<DisclosureDocument> searchResponse = elasticsearchClient.search(searchRequest, DisclosureDocument.class);
        List<Hit<DisclosureDocument>> hits = searchResponse.hits().hits();

        return hits.stream()
                .map(Hit::source).filter(Objects::nonNull)
                .map(DisclosureResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // rceptNo를 사용하여 보고서 상세 조회 기능
    public DisclosureResponseDto getDisclosureById(String corporateReportId) throws IOException {
        // Elasticsearch에서 특정 문서를 조회
        var getRequest = co.elastic.clients.elasticsearch.core.GetRequest.of(g -> g
                .index("disclosures")   // 인덱스명
                .id(corporateReportId)  // rceptNo로 조회
        );

        // Elasticsearch에서 조회 결과 반환
        var getResponse = elasticsearchClient.get(getRequest, DisclosureDocument.class);

        // 조회된 문서가 있을 경우 반환, 없으면 null
        if (getResponse.found()) {
            return DisclosureResponseDto.fromEntity(getResponse.source());
        } else {
            log.warn("DisclosureDocument with rceptNo [{}] not found.", corporateReportId);
            return null;
        }
    }
}