package com.tradingtrends.coin.domain.repository;

import com.tradingtrends.coin.domain.model.CoinInfo;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinRepository extends JpaRepository<CoinInfo, UUID> {

}
