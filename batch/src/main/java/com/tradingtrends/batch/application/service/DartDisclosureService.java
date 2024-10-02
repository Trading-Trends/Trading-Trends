package com.tradingtrends.batch.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingtrends.batch.domain.model.Entity.Disclosure;
import com.tradingtrends.batch.domain.repository.DisclosureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Service
public class DartDisclosureService {

    @Value("${dart.api-key}")
    private String apiKey;

    @Value("${dart.list-url}")
    private String listUrl;

    private final DisclosureRepository disclosureRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();


    public DartDisclosureService(DisclosureRepository disclosureRepository) {
        this.disclosureRepository = disclosureRepository;
    }

    public List<Disclosure> fetchDisclosuresToday() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return fetchDisclosures(today, today);
    }

    public void saveDisclosures(List<Disclosure> disclosures) {
        disclosureRepository.saveAll(disclosures);
        log.info("Saved " + disclosures.size() + " disclosures");
    }

    public List<String> findRceptNoByLoadDtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay) {// 조회할 날짜
        List<String> rceptNos = disclosureRepository.findRceptNoByLoadDtBetween(startOfDay, endOfDay);
        log.info("Fetched " + rceptNos.size() + " rceptNos");
        return rceptNos;
    }

    public List<Disclosure> fetchDisclosures(String bgn_de, String end_de) {
        String apiUrl = String.format("%s?crtfc_key=%s&bgn_de=%s&end_de=%s&pblntf_ty=A", listUrl, apiKey, bgn_de, end_de);
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        List<Disclosure> disclosures = new ArrayList<>();
        log.info("Fetched Disclosures Response: " + response);
        try {
            // JSON 파싱 및 Disclosure 변환
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode resultNode = rootNode.path("list");

            for (JsonNode item : resultNode) {
                Disclosure entity = new Disclosure();
                entity.setRceptNo(item.path("rcept_no").asText());
                entity.setCorpName(item.path("corp_name").asText());
                entity.setCorpCode(item.path("corp_code").asText());
                entity.setReportNm(item.path("report_nm").asText());
                entity.setRceptDt(item.path("rcept_dt").asText());
                entity.setLoadDt(LocalDateTime.now());
                log.info("Entity: " + entity);
                disclosures.add(entity);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return disclosures;
    }
}
