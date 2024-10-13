package com.tradingtrends.stock.application.dto;

import com.tradingtrends.stock.domain.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private String pdno;
    private String prdtTypeCd;
    private String mketIdCd;
    private String sctyGrpIdCd;
    private String excgDvsnCd;
    private Long lstgStqt;
    private Long lstgCptlAmt;
    private Long cpta;
    private Long papr;
    private String kospi200ItemYn;
    private LocalDateTime sctsMketLstgDt;
    private LocalDateTime kosdaqMketLstgDt;
    private LocalDateTime frbdMketLstgDt;
    private String prdtName;
    private String prdtEngName;
    private Long thdtClpr;
    private Long bfdyClpr;
    private LocalDateTime clprChngDt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StockResponse fromEntity(Stock entity) {
        return StockResponse.builder()
                .pdno(entity.getPdno())
                .prdtTypeCd(entity.getPrdtTypeCd())
                .mketIdCd(entity.getMketIdCd())
                .sctyGrpIdCd(entity.getSctyGrpIdCd())
                .excgDvsnCd(entity.getExcgDvsnCd())
                .lstgStqt(entity.getLstgStqt())
                .lstgCptlAmt(entity.getLstgCptlAmt())
                .cpta(entity.getCpta())
                .papr(entity.getPapr())
                .kospi200ItemYn(entity.getKospi200ItemYn())
                .sctsMketLstgDt(entity.getSctsMketLstgDt())
                .kosdaqMketLstgDt(entity.getKosdaqMketLstgDt())
                .frbdMketLstgDt(entity.getFrbdMketLstgDt())
                .prdtName(entity.getPrdtName())
                .prdtEngName(entity.getPrdtEngName())
                .thdtClpr(entity.getThdtClpr())
                .bfdyClpr(entity.getBfdyClpr())
                .clprChngDt(entity.getClprChngDt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
