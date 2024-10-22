package com.tradingtrends.corporate.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "P_INTERESTED_CORPORATE", schema = "s_corporate")
public class InterestedCorporate extends TimeStamped {
    @Id
    @GeneratedValue
    @Column(name = "interested_corporate_id", nullable = false)
    private UUID id;
    private Long userId;          // 사용자 ID
    private String corpCode;      // 고유번호

    public InterestedCorporate(UUID id, Long userId, String corpCode, boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.corpCode = corpCode;
        setDeleted(isDeleted);
    }
}
