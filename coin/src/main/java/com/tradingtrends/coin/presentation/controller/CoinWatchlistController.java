package com.tradingtrends.coin.presentation.controller;

import com.tradingtrends.coin.application.dto.CoinWatchlistResponseDto;
import com.tradingtrends.coin.application.service.CoinWatchlistService;
import com.tradingtrends.coin.presentation.request.CoinWatchlistRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/interested-coin")
public class CoinWatchlistController {

    private final CoinWatchlistService coinWatchlistService;

    /**
     * 관심 종목 추가
     * @param coinId
     * @param request
     * @return
     */
    @PostMapping("/{coin_id}")
    public ResponseEntity<CoinWatchlistResponseDto> createCoinWatchlist(
        @PathVariable("coin_id") UUID coinId,
        @RequestBody CoinWatchlistRequest request
    ){
        return ResponseEntity.status(HttpStatus.OK).body(coinWatchlistService.createCoinWatchlist(coinId, request));
    }

    @GetMapping
    public ResponseEntity<Page<CoinWatchlistResponseDto>> getCoinsWatchlist(
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.ASC) Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK).body(coinWatchlistService.getCoinsWatchlist(pageable));
    }

    /**
     * 관심 종목 상한가, 하한가 수정
     * @return
     */
    @PatchMapping("/{watchlist_coin_id}")
    public ResponseEntity<CoinWatchlistResponseDto> updateCoinWatchlist(
        @PathVariable("watchlist_coin_id") UUID watchlistCoinId,
        @RequestBody CoinWatchlistRequest request
    ){
        return ResponseEntity.status(HttpStatus.OK).body(coinWatchlistService.updateCoinWatchlist(watchlistCoinId, request));
    }

    /**
     * 관심 종목 삭제
     * @return
     */
    @DeleteMapping("/{watchlist_coin_id}")
    public ResponseEntity<Void> deleteCoinWatchlist(
        @PathVariable("watchlist_coin_id") UUID watchlistCoinId
    ){
        coinWatchlistService.deleteCoinWatchlist(watchlistCoinId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
