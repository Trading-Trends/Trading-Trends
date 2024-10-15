package com.tradingtrends.stock.application.service;

import com.tradingtrends.stock.application.dto.StockWatchlistRequest;
import com.tradingtrends.stock.application.dto.StockWatchlistResponse;
import com.tradingtrends.stock.domain.model.Stock;
import com.tradingtrends.stock.domain.model.WatchlistStock;
import com.tradingtrends.stock.domain.repository.StockRepository;
import com.tradingtrends.stock.domain.repository.WatchlistStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StockWatchlistService {
    private final StockRepository stockRepository;
    private final WatchlistStockRepository watchlistStockRepository;

    @Transactional
    public StockWatchlistResponse createStockWatchlist(String pdno, StockWatchlistRequest stockWatchlistRequest) {
        Stock stock = stockRepository.findById(pdno)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주식이 존재하지 않습니다."));

        WatchlistStock watchlistStock = WatchlistStock.builder()
                .upperLimitPrice(stockWatchlistRequest.getUpperLimitPrice())
                .lowerLimitPrice(stockWatchlistRequest.getLowerLimitPrice())
                .stock(stock)
                .build();

        return StockWatchlistResponse.fromEntity(watchlistStockRepository.save(watchlistStock));
    }

    public Page<StockWatchlistResponse> getStocksWatchlist(Pageable pageable) {
        return watchlistStockRepository.findAll(pageable).map(StockWatchlistResponse::fromEntity);
    }

    @Transactional
    public StockWatchlistResponse updateStockWatchlist(UUID watchlistId, StockWatchlistRequest request) {
        WatchlistStock watchlistStock = watchlistStockRepository.findById(watchlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "관심 종목이 존재하지 않습니다."));
        watchlistStock.updateLimitPrice(request);
        return StockWatchlistResponse.fromEntity(watchlistStock);
    }

    @Transactional
    public void deleteStockWatchlist(UUID watchlistId) {
        if (!watchlistStockRepository.existsById(watchlistId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 관심 종목이 존재하지 않습니다.");
        }
        watchlistStockRepository.deleteById(watchlistId);
    }
}
