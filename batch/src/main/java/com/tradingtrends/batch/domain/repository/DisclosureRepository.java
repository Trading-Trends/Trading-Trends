package com.tradingtrends.batch.domain.repository;

import com.tradingtrends.batch.domain.model.Entity.Disclosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DisclosureRepository extends JpaRepository<Disclosure, UUID> {

    @Query("SELECT d.rceptNo FROM Disclosure d WHERE d.rceptDt = :today")
    List<String> findRceptNosForToday();

    @Query("SELECT d.rceptNo FROM Disclosure d WHERE d.loadDt BETWEEN :startDate AND :endDate")
    List<String> findRceptNoByLoadDtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
