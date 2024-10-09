package com.tradingtrends.corporate.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "P_CORPORATE_FINANCE", schema = "s_corporate")
public class CorporateMajorFinance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "corp_id")
    private UUID id;
    private String bsnsYear;      // 사업 연도
    private String corpCode;      // 고유번호
    private String stockCode;     // 종목코드
    private String reprtCode;     // 보고서 코드
    private String idxClCode;     // 지표분류코드
    private String idxClNm;       // 지표분류명
    private String idxCode;       // 지표코드
    private String idxNm;         // 지표명
    private String idxVal;        // 지표값
}
