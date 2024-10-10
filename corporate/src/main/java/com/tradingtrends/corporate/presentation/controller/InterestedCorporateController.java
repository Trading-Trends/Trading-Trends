package com.tradingtrends.corporate.presentation.controller;

import com.tradingtrends.corporate.application.dto.InterestedCorporateResponseDto;
import com.tradingtrends.corporate.application.service.InterestedCorporateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interested-corporate")
public class InterestedCorporateController {

    private final InterestedCorporateService interestedCorporateService;

    @PostMapping
    public UUID createInterestedCorporate(@RequestParam(value = "corp_code") String corpCode) {
        Long userId = 01L;
        return interestedCorporateService.createInterestedCorporate(userId, corpCode);
    }

    @DeleteMapping("/{interested_corporate_id}")
    public UUID deleteInterestedCorporate(@PathVariable UUID interested_corporate_id) {
        Long userId = 01L;
        return interestedCorporateService.deleteInterestedCorporate(userId, interested_corporate_id);
    }

    @GetMapping("/{user_id}")
    public Page<InterestedCorporateResponseDto> getInterestedCorporateList(@PathVariable("user_id") Long userId,
                                                                          @RequestParam(defaultValue = "1") int page,
                                                                          @RequestParam(defaultValue = "createdAt") String sort){
        // user_role이 관리자가 아니라면
        // 로그인 사용자 가져와서 userId와 비교 후 다르면 exception 처리
        Sort.Direction sortDirection = Sort.Direction.fromString("desc");
        Sort sortOption = Sort.by(sortDirection, sort);
        // size가 10, 20, 30이 아닌 경우 10으로 조정
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, sortOption);
        return interestedCorporateService.getInterestedCorporateList(userId, pageable);

    }
}
