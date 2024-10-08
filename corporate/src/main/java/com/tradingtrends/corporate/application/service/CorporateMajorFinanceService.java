package com.tradingtrends.corporate.application.service;

import com.tradingtrends.corporate.application.dto.CorporateMajorFinanceResponseDto;
import com.tradingtrends.corporate.application.dto.CorporateMajorFinanceSearchRequestDto;
import com.tradingtrends.corporate.domain.model.entity.CorporateMajorFinance;
import com.tradingtrends.corporate.domain.repository.CorporateMajorFinanceRepository;
import com.tradingtrends.corporate.domain.specification.CorporateMajorFinanceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CorporateMajorFinanceService {
    private final CorporateMajorFinanceRepository corporateMajorFinanceRepository;

    public CorporateMajorFinanceResponseDto getCorporationMajorFinance(UUID corpId) {
        CorporateMajorFinance corporateMajorFinance = corporateMajorFinanceRepository.findById(corpId).orElseThrow(() -> new IllegalArgumentException("해당하는 기업주요재무지표가 없습니다."));
        CorporateMajorFinanceResponseDto corporateMajorFinanceResponseDto = new CorporateMajorFinanceResponseDto(corporateMajorFinance);
        return corporateMajorFinanceResponseDto;
    }

    public Page<CorporateMajorFinanceResponseDto> searchCorporationMajorFinance(CorporateMajorFinanceSearchRequestDto searchRequestDto, Pageable pageable) {
        Page<CorporateMajorFinance> corporateFinancePage = corporateMajorFinanceRepository.findAll(CorporateMajorFinanceSpecification.searchWith(searchRequestDto),pageable);
        Page<CorporateMajorFinanceResponseDto> corporateFinanceDtoPage = corporateFinancePage.map(corporateMajorFinance -> new CorporateMajorFinanceResponseDto(corporateMajorFinance));
        return corporateFinanceDtoPage;
    }
}
