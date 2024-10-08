package com.tradingtrends.corporate.presentation.controller;


import com.tradingtrends.corporate.application.dto.DisclosureResponseDto;
import com.tradingtrends.corporate.application.service.DisclosureSearchService;
import com.tradingtrends.corporate.domain.model.entity.DisclosureDocument;
import com.tradingtrends.corporate.presentation.request.DisclosureSearchRequestDto;
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
    public ResponseEntity<List<DisclosureResponseDto>> searchCorporateReport(
            @Valid @ModelAttribute DisclosureSearchRequestDto requestDto) {

        try {
            List<DisclosureResponseDto> results = searchService.searchDisclosure(requestDto);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            log.error("Error while searching for disclosures: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{corporate_report_id}")
    public ResponseEntity<DisclosureResponseDto> searchCorporateReport(@PathVariable("corporate_report_id") String corporateReportId) {
        try {
            DisclosureResponseDto result = searchService.getDisclosureById(corporateReportId);
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
