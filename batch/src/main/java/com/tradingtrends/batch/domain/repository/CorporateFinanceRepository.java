package com.tradingtrends.batch.domain.repository;

import com.tradingtrends.batch.domain.model.CorporateMajorFinance;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorporateFinanceRepository extends JpaRepository<CorporateMajorFinance, UUID> {

    Optional<CorporateMajorFinance> findByCorpCodeAndBsnsYearAndReprtCodeAndIdxNm(String corpCode, String bsnsYear, String reprtCode, String idxNm);

}
