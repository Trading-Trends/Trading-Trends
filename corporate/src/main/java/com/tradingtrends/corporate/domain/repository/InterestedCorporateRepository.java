package com.tradingtrends.corporate.domain.repository;

import com.tradingtrends.corporate.domain.model.InterestedCorporate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InterestedCorporateRepository extends JpaRepository<InterestedCorporate, UUID> {

    Page<InterestedCorporate> findByUserIdAndIsDeleted(Long userId, boolean isDeleted, Pageable pageable);
}
