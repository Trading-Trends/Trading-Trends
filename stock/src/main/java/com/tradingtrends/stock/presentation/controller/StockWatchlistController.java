package com.tradingtrends.stock.presentation.controller;

import com.tradingtrends.stock.application.dto.StockWatchlistRequest;
import com.tradingtrends.stock.application.dto.StockWatchlistResponse;
import com.tradingtrends.stock.application.service.StockWatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/interested-stock")
public class StockWatchlistController {
    private final StockWatchlistService stockWatchlistService;

    // 관심 종목 추가
    @PostMapping("/{pdno}")
    public ResponseEntity<StockWatchlistResponse> createStockWatchlist(
            @PathVariable("pdno") String pdno,
            @RequestBody StockWatchlistRequest stockWatchlistRequest
    ) {
        return ResponseEntity.ok(stockWatchlistService.createStockWatchlist(pdno, stockWatchlistRequest));
    }

    // 관심 종목 전체 조회
    @GetMapping
    public ResponseEntity<Page<StockWatchlistResponse>> getStocksWatchlist(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockWatchlistService.getStocksWatchlist(pageable));
    }

    // 관심 종목 수정 (상한가, 하한가)
    @PatchMapping("/{watchlist_id}")
    public ResponseEntity<StockWatchlistResponse> updateStockWatchlist(
            @PathVariable("watchlist_id") UUID watchlistId,
            @RequestBody StockWatchlistRequest stockWatchlistRequest
    ) {
        return ResponseEntity.ok(stockWatchlistService.updateStockWatchlist(watchlistId, stockWatchlistRequest));
    }

    // 관심 종목 삭제
    @DeleteMapping("/{watchlist_id}")
    public ResponseEntity<Void> deleteStockWatchlist(@PathVariable("watchlist_id") UUID watchlistId) {
        stockWatchlistService.deleteStockWatchlist(watchlistId);
        return ResponseEntity.ok().build();
    }
}
