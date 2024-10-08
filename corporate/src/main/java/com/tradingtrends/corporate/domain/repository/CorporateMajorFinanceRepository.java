package com.tradingtrends.corporate.domain.repository;

import com.tradingtrends.corporate.domain.model.entity.CorporateMajorFinance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CorporateMajorFinanceRepository extends JpaRepository<CorporateMajorFinance, UUID>, JpaSpecificationExecutor<CorporateMajorFinance> {

}
