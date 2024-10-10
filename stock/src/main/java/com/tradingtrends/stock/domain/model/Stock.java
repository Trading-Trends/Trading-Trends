package com.tradingtrends.stock.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "P_STOCK", schema = "s_stock")
public class Stock {
    @Id
    @Column(nullable = false)
    private String pdno; // 상품번호 - 주식의 고유한 식별자

    @Column(nullable = false)
    private String prdtTypeCd; // 상품유형코드 - 주식, ETF, ETN 등을 식별

    @Column(nullable = false)
    private String mketIdCd; // 시장ID코드- 주식이 거래되는 시장을 구분 (ex. 유가증권, 코스닥 등)

    private String sctyGrpIdCd; // 증권그룹ID코드 - 특정 증권의 종류
    private String excgDvsnCd; // 거래소구분코드 - 주식이 거래되는 거래소

    private Long lstgStqt; // 상장주수
    private Long lstgCptlAmt; // 상장자본금액
    private Long cpta; // 자본금
    private Long papr; // 액면가

    private String kospi200ItemYn; // 코스피200 종목 여부
    private LocalDateTime sctsMketLstgDt; // 유가증권시장 상장일자
    private LocalDateTime kosdaqMketLstgDt; // 코스닥시장 상장일자
    private LocalDateTime frbdMketLstgDt; // 프리보드시장 상장일자

    private String prdtName; // 상품명
    private String prdtEngName; // 상품영문명

    private Long thdtClpr; // 당일종가
    private Long bfdyClpr; // 전일종가
    private LocalDateTime clprChngDt; // 종가변경일자

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
