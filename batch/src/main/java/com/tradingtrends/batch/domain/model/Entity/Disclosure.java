package com.tradingtrends.batch.domain.model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "disclosure", schema = "s_corporate")
public class Disclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "rcept_no", unique = true, nullable = false)
    private String rceptNo;

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "corp_code")
    private String corpCode;

    @Column(name = "report_nm")
    private String reportNm;

    @Column(name = "rcept_dt")
    private String rceptDt;

    @Column(name = "load_dt")
    private LocalDateTime loadDt;
}
