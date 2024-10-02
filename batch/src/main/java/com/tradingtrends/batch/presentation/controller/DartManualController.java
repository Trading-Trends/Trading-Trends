package com.tradingtrends.batch.presentation.controller;

import com.tradingtrends.batch.application.service.DartDisclosureService;
import com.tradingtrends.batch.application.service.DartToElasticsearchService;
import com.tradingtrends.batch.domain.model.Entity.Disclosure;
import com.tradingtrends.batch.domain.repository.DisclosureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dart-manual")
public class DartManualController {

    private final DartDisclosureService dartDisclosureService;
    private final DartToElasticsearchService dartToElasticsearchService;

    @GetMapping("/collectDisclosures")
    public void collectDisclosures(@RequestParam String bgn_de, @RequestParam String end_de) throws IOException {
        // 1. 주어진 기간의 공시 데이터를 수집하고 저장
        List<Disclosure> disclosures= dartDisclosureService.fetchDisclosures(bgn_de, end_de);
        dartDisclosureService.saveDisclosures(disclosures);
        // 2. 오늘 날짜의 시작과 끝 계산
        LocalDate today = LocalDate.now(); // 오늘 날짜를 가져옴
        LocalDateTime startOfToday = today.atStartOfDay(); // 오늘의 시작 시간 (00:00:00)
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX); // 오늘의 끝 시간 (23:59:59.999999999)

        // 3. 오늘 날짜의 공시 데이터를 가져옴
        List<String> rceptNos = dartDisclosureService.findRceptNoByLoadDtBetween(startOfToday, endOfToday);

        // 4. 공시 번호로 엘라스틱서치에 저장
        for (String rceptNo : rceptNos) {
            dartToElasticsearchService.fetchDocumentAndSaveToElasticsearch(rceptNo);
        }
    }
}
