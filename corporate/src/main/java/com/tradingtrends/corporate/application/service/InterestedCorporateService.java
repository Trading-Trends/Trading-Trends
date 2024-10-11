package com.tradingtrends.corporate.application.service;

import com.tradingtrends.corporate.application.dto.InterestedCorporateResponseDto;
import com.tradingtrends.corporate.domain.model.InterestedCorporate;
import com.tradingtrends.corporate.domain.repository.InterestedCorporateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterestedCorporateService {
    private final InterestedCorporateRepository interestedCorporateRepository;

    public UUID createInterestedCorporate(Long userId, String corpCode) {
        InterestedCorporate interestedCorporate = new InterestedCorporate(UUID.randomUUID(), userId, corpCode, false);
        interestedCorporateRepository.save(interestedCorporate);
        return interestedCorporate.getId();
    }

    @Transactional
    public UUID deleteInterestedCorporate(Long userId, UUID interestedCorporateId) {
        InterestedCorporate interestedCorporate = interestedCorporateRepository.findById(interestedCorporateId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 관심 기업이 없습니다."));
        interestedCorporate.setDeleted(true);
        return interestedCorporateId;
    }

    public  Page<InterestedCorporateResponseDto> getInterestedCorporateList(Long userId, Pageable pageable) {
        Page<InterestedCorporate> interestedCorporatePage = interestedCorporateRepository.findByUserIdAndIsDeleted(userId, false, pageable);
        Page<InterestedCorporateResponseDto> interestedCorporateResponseDtoPage =interestedCorporatePage.map(InterestedCorporateResponseDto::new);
        return interestedCorporateResponseDtoPage;
    }



}
