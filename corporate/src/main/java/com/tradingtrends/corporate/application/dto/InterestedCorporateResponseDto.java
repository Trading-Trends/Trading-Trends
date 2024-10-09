package com.tradingtrends.corporate.application.dto;

import com.tradingtrends.corporate.domain.model.InterestedCorporate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class InterestedCorporateResponseDto {
    private UUID id;
    private Long userId;
    private String corpCode;      // 고유번호

    public InterestedCorporateResponseDto(InterestedCorporate interestedCorporate) {
        this.id = interestedCorporate.getId();
        this.userId = interestedCorporate.getUserId();
        this.corpCode = interestedCorporate.getCorpCode();
    }
}
