package com.tradingtrends.corporate.application.dto;

import com.tradingtrends.corporate.domain.model.DisclosureDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DisclosureResponseDto {

    private String rceptNo;
    private String corpName;
    private String corpCode;
    private String reportNm;
    private String rceptDt;
    private LocalDateTime loadDt;
    private String rawXmlData;

    // DisclosureDocument 엔티티를 DTO로 변환하는 정적 메서드
    public static DisclosureResponseDto fromEntity(DisclosureDocument entity) {
        return new DisclosureResponseDto(
                entity.getRceptNo(),
                entity.getCorpName(),
                entity.getCorpCode(),
                entity.getReportNm(),
                entity.getRceptDt(),
                entity.getLoadDt(),
                entity.getRawXmlData()
        );
    }
}