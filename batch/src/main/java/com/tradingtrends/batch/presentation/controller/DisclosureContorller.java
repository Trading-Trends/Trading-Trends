package com.tradingtrends.batch.presentation.controller;


import com.tradingtrends.batch.application.service.DisclosureSearchService;
import com.tradingtrends.batch.domain.model.Entity.DisclosureDocument;
import com.tradingtrends.batch.presentation.request.DisclosureSearchRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/corporate-report")
@RequiredArgsConstructor
public class DisclosureContorller {

    private final DisclosureSearchService searchService;

    // 검색 API 엔드포인트
    @GetMapping
    public ResponseEntity<List<DisclosureDocument>> searchCorporateReport(
            @Valid @ModelAttribute DisclosureSearchRequestDto requestDto) {

        try {
            List<DisclosureDocument> results = searchService.searchDisclosure(requestDto);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            log.error("Error while searching for disclosures: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{corporate_report_id}")
    public ResponseEntity<DisclosureDocument> searchCorporateReport(@PathVariable("corporate_report_id") String corporateReportId) {
        try {
            DisclosureDocument result = searchService.getDisclosureById(corporateReportId);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Error while fetching the disclosure report: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}
