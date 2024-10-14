package com.tradingtrends.corporate.presentation.controller;

import com.tradingtrends.corporate.application.dto.CorporateMajorFinanceResponseDto;
import com.tradingtrends.corporate.application.dto.CorporateMajorFinanceSearchRequestDto;
import com.tradingtrends.corporate.application.service.CorporateMajorFinanceService;
import com.tradingtrends.corporate.application.service.CorporateMajorFinanceViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/corporate-major-finance")
public class CorporateMajorFinanceController {

    private final CorporateMajorFinanceService corporateMajorFinanceService;
    private final CorporateMajorFinanceViewCountService corporateMajorFinanceViewCountService;

    @GetMapping("/{corporate_major_finance_id}")
    public CorporateMajorFinanceResponseDto getCorporationMajorFinance(@PathVariable(name = "corporate_major_finance_id") UUID corporateMajorFinanceId){
        CorporateMajorFinanceResponseDto responseDto = corporateMajorFinanceService.getCorporationMajorFinance(corporateMajorFinanceId);
        // corpCode로 조회수 증가
        corporateMajorFinanceViewCountService.incrementViewCount(responseDto.getCorpCode());
        return responseDto;
    }

    //검색 필드: corp_code, stock_code, bsns_year
    @GetMapping()
    public Page<CorporateMajorFinanceResponseDto> searchCorporationMajorFinance(@RequestParam(name = "corp_code", required = false) String corpCode,
                                                                                @RequestParam(name = "stock_code", required = false) String stockCode,
                                                                                @RequestParam(name = "from_bsns_year", required = false) String fromBsnsYear,
                                                                                @RequestParam(name = "to_bsns_year", required = false) String toBsnsYear,
                                                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                @RequestParam(value = "sort", defaultValue = "bsnsYear") String sort,
                                                                                @RequestParam(value = "direction", defaultValue = "desc") String direction){
        CorporateMajorFinanceSearchRequestDto searchRequestDto = new CorporateMajorFinanceSearchRequestDto(corpCode, stockCode, fromBsnsYear, toBsnsYear);
        // 정렬 순서 및 방향 설정
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortOption = Sort.by(sortDirection, sort);
        // size가 10, 20, 30이 아닌 경우 10으로 조정
        if (size != 10 && size != 20 && size != 30) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page - 1, size, sortOption);
        Page<CorporateMajorFinanceResponseDto> CorporateFinanceDtoPage = corporateMajorFinanceService.searchCorporationMajorFinance(searchRequestDto, pageable);
        return CorporateFinanceDtoPage;
    }
    // 상위 N개의 corp_code 조회
    @GetMapping("/top-corp-codes")
    public List<String> getTopCorpCodes(@RequestParam(value = "size", defaultValue = "10") int size) {
        return corporateMajorFinanceViewCountService.getTopNCorpCodes(size);
    }
}
