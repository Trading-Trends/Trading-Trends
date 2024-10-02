package com.tradingtrends.batch.presentation.controller;

import com.tradingtrends.batch.application.dto.CorporateCodesResponseDto;
import com.tradingtrends.batch.application.dto.DartCorporateFinanceApiResponse;
import com.tradingtrends.batch.application.service.CorporateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/corporate-finance")
public class CorporateController {

    private final CorporateService corporateService;

    @GetMapping("/fetch-corp-code-info")
    public ResponseEntity<List<CorporateCodesResponseDto>> fetchCorpCodeInfo()
        throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(corporateService.fetchAndSaveCorpCodeInfo());
    }

    @GetMapping("/fetch-corp-finance-info")
    public ResponseEntity<List<DartCorporateFinanceApiResponse.CorporateFinanceDto>> fetchCorpCodeFinanceInfo()
        throws Exception {
        return  ResponseEntity.status(HttpStatus.OK).body(corporateService.fetchAndSaveCorpFinanceInfo());
    }




}
