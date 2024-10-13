package com.tradingtrends.stock.application.service;

import com.tradingtrends.stock.application.dto.StockRequest;
import com.tradingtrends.stock.application.dto.StockResponse;
import com.tradingtrends.stock.domain.model.Stock;
import com.tradingtrends.stock.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {
    @Value("${stock-api.appkey}")
    private String appKey;

    @Value("${stock-api.appsecret}")
    private String appSecret;

    private final StockOAuthTokenService stockOAuthTokenService;
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/search-stock-info";

    public void saveStockInfo(StockRequest stockRequest) throws InterruptedException {
        String accessToken = stockOAuthTokenService.getAccessToken(appKey, appSecret);
        List<String> pdnoList = Arrays.asList(stockRequest.getPdno().split(","));

        // 초당 호출 제한이 있어서 20개씩 나눠서 호출 (실전투자는 초당 20건, 모의투자는 초당 5건)
        int batchSize = 20;
        for (int i = 0; i < pdnoList.size(); i += batchSize) {
            List<String> batch = pdnoList.subList(i, Math.min(i + batchSize, pdnoList.size()));

            // 각 배치에 대한 API 호출 처리
            for (String pdno : batch) {
                Stock stock = fetchStockInfo(pdno, stockRequest.getPrdtTypeCd(), accessToken);
                stockRepository.save(stock);
            }

            Thread.sleep(1000); // 1초 대기
        }
    }

    public StockResponse getStockInfo(String pdno) {
        Stock stock = stockRepository.findById(pdno)
                .orElseThrow(() -> new IllegalArgumentException(pdno + "을 찾을 수 없습니다."));

        return StockResponse.fromEntity(stock);
    }

    private Stock fetchStockInfo(String pdno, String prdtTypeCd, String accessToken) {
        // API 요청 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + accessToken);
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);
        headers.set("tr_id", "CTPF1002R");
        headers.set("custtype", "P");

        // API 요청 본문은 필요하지 않으므로 빈 내용으로 구성
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        // API 호출 URL 구성 (쿼리 파라미터로 pdno와 prdtTypeCd 포함)
        String url = API_URL + "?PRDT_TYPE_CD=" + prdtTypeCd + "&PDNO=" + pdno;

        // API 호출
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);

        // 응답에서 필요한 데이터 추출하여 Stock 객체 생성
        Map<String, Object> output = (Map<String, Object>) response.getBody().get("output");

        // 날짜 파싱
        LocalDateTime sctsMketLstgDt = parseDateTime((String) output.get("scts_mket_lstg_dt"));
        LocalDateTime kosdaqMketLstgDt = parseDateTime((String) output.get("kosdaq_mket_lstg_dt"));
        LocalDateTime frbdMketLstgDt = parseDateTime((String) output.get("frbd_mket_lstg_dt"));
        LocalDateTime clprChngDt = parseDateTime((String) output.get("clpr_chng_dt"));

        return Stock.builder()
                .pdno((String) output.get("pdno"))
                .prdtTypeCd((String) output.get("prdt_type_cd"))
                .mketIdCd((String) output.get("mket_id_cd"))
                .sctyGrpIdCd((String) output.get("scty_grp_id_cd"))
                .excgDvsnCd((String) output.get("excg_dvsn_cd"))
                .lstgStqt(Long.parseLong((String) output.get("lstg_stqt")))
                .lstgCptlAmt(Long.parseLong((String) output.get("lstg_cptl_amt")))
                .cpta(Long.parseLong((String) output.get("cpta")))
                .papr(Long.parseLong((String) output.get("papr")))
                .kospi200ItemYn((String) output.get("kospi200_item_yn"))
                .sctsMketLstgDt(sctsMketLstgDt)
                .kosdaqMketLstgDt(kosdaqMketLstgDt)
                .frbdMketLstgDt(frbdMketLstgDt)
                .prdtName((String) output.get("prdt_name"))
                .prdtEngName((String) output.get("prdt_eng_name"))
                .thdtClpr(Long.parseLong((String) output.get("thdt_clpr")))
                .bfdyClpr(Long.parseLong((String) output.get("bfdy_clpr")))
                .clprChngDt(clprChngDt)
                .build();
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
    }
}