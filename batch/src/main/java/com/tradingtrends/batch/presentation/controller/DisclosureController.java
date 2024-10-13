package com.tradingtrends.batch.presentation.controller;

import com.tradingtrends.batch.application.service.DartDisclosureService;
import com.tradingtrends.batch.application.service.DartToElasticsearchService;
import com.tradingtrends.batch.domain.model.Entity.Disclosure;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/data-collection/disclosure")
public class DisclosureController {

    private final DartDisclosureService dartDisclosureService;
    private final DartToElasticsearchService dartToElasticsearchService;

    @GetMapping
    public void collectDisclosures(@RequestParam String bgn_de, @RequestParam String end_de) throws IOException {
        // 1. 주어진 기간의 공시 데이터를 수집하고 저장
        List<Disclosure> disclosures= dartDisclosureService.fetchDisclosures(bgn_de, end_de);
        dartDisclosureService.saveDisclosures(disclosures);

        // 2. 오늘 날짜의 공시 데이터를 가져옴
        for (Disclosure disclosure : disclosures) {
            dartToElasticsearchService.fetchDocumentAndSaveToElasticsearch(disclosure.getRceptNo(), disclosure);
        }
    }
}
