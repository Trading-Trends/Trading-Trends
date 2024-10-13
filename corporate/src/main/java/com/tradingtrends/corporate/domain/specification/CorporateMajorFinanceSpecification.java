package com.tradingtrends.corporate.domain.specification;

import com.tradingtrends.corporate.application.dto.CorporateMajorFinanceSearchRequestDto;
import com.tradingtrends.corporate.domain.model.CorporateMajorFinance;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class CorporateMajorFinanceSpecification {
    public static Specification<CorporateMajorFinance> searchWith(CorporateMajorFinanceSearchRequestDto searchRequestDto) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();  // 기본 조건 없음


            // 검색 조건 1: corp_code가 null이 아닌 경우, corp_code와 일치하는 레코드 추가
            if (searchRequestDto.getCorpCode()!= null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("corpCode"), searchRequestDto.getCorpCode()));
            }

            // 검색 조건 2: stock_code가 null이 아닌 경우, stock_code와 일치하는 레코드 추가
            if (searchRequestDto.getStockCode() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("stockCode"), searchRequestDto.getStockCode()));
            }

            // 검색 조건 3: from_bsns_year, to_bsns_year가 null이 아니면 from_bsns_year~to_bsns_year 사이에 있는 레코드 추가
            //            from_bsns_year가 null 이 아니고 to_bsns_year가 null이면 from_bsns_year 이후의 레코드 추가
            //            from_bsns_year가 null 이고 to_bsns_year가 null이 아니면 to_bsns_year 이전의 레코드 추가
            if (searchRequestDto.getFromBsnsYear() != null || searchRequestDto.getToBsnsYear() != null) {
                if (searchRequestDto.getFromBsnsYear() != null && searchRequestDto.getToBsnsYear() != null) {
                    // from_bsns_year ~ to_bsns_year 사이에 있는 레코드
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.between(root.get("bsnsYear"),
                                    searchRequestDto.getFromBsnsYear(),
                                    searchRequestDto.getToBsnsYear()));
                } else if (searchRequestDto.getFromBsnsYear() != null) {
                    // from_bsns_year 이후의 레코드
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.greaterThanOrEqualTo(root.get("bsnsYear"),
                                    searchRequestDto.getFromBsnsYear()));
                } else if (searchRequestDto.getToBsnsYear() != null) {
                    // to_bsns_year 이전의 레코드
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.lessThanOrEqualTo(root.get("bsnsYear"),
                                    searchRequestDto.getToBsnsYear()));
                }
            }

            return predicate; // 최종적으로 생성된 조건 반환
        };
    }
}
