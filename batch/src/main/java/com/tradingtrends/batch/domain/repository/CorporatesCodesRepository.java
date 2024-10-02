package com.tradingtrends.batch.domain.repository;

import com.tradingtrends.batch.domain.model.CorporateCodes;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CorporatesCodesRepository extends JpaRepository<CorporateCodes, String> {

    @Query("select c.corpCode from CorporateCodes c")
    List<String> findAllCorpCodes();

    @Query("SELECT c.corpCode FROM CorporateCodes c WHERE c.stockCode IS NOT NULL "
        + "AND TRIM(c.stockCode) <> '' "
        + "AND c.isStockCodeChecked = false "
        + "AND SUBSTRING(c.modifyDate, 1, 4) IN ('2020', '2021', '2022', '2023', '2024')")
    List<String> findAllCorpCodesAndStockCodeIsNotNullInYear(Pageable pageable);

    List<CorporateCodes> findByCorpCodeIn(List<String> corpCodes);

}
