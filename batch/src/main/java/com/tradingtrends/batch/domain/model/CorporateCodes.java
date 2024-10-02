package com.tradingtrends.batch.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "P_CORPORATE_CODES", schema = "s_corporate")
public class CorporateCodes {

    @Id
    private String corpCode;   // 공시 대상 회사의 고유번호 (8자리)
    private String corpName;   // 정식명칭
    private String stockCode;  // 상장회사인 경우 주식의 종목코드 (6자리)
    private String modifyDate; // 최종변경일자 (YYYYMMDD)

    private boolean isStockCodeChecked = false;

    public void markStockCodeChecked() {
        this.isStockCodeChecked = true;
    }
}
