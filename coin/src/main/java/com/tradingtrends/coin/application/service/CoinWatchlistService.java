package com.tradingtrends.coin.application.service;

import static com.tradingtrends.coin.domain.model.WatchlistCoin.*;

import com.tradingtrends.coin.application.dto.CoinApiResponseDto;
import com.tradingtrends.coin.application.dto.CoinWatchlistResponseDto;
import com.tradingtrends.coin.domain.model.CoinInfo;
import com.tradingtrends.coin.domain.model.WatchlistCoin;
import com.tradingtrends.coin.domain.repository.CoinRepository;
import com.tradingtrends.coin.domain.repository.WatchlistCoinRepository;
import com.tradingtrends.coin.presentation.request.CoinWatchlistRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CoinWatchlistService {

    private final CoinRepository coinRepository;
    private final WatchlistCoinRepository watchlistCoinRepository;

    @Transactional
    public CoinWatchlistResponseDto createCoinWatchlist(UUID coinId, CoinWatchlistRequest request) {
        CoinInfo coinInfo = coinRepository.findById(coinId).orElseThrow(IllegalArgumentException::new);
        return CoinWatchlistResponseDto.fromEntity(
            watchlistCoinRepository.save(addWatchlistCoin(request, coinInfo))
        );
    }

    public Page<CoinWatchlistResponseDto> getCoinsWatchlist(Pageable pageable) {
        Page<WatchlistCoin> watchlistCoins = watchlistCoinRepository.findAll(pageable);
        return watchlistCoins.map(CoinWatchlistResponseDto::fromEntity);
    }

    @Transactional
    public CoinWatchlistResponseDto updateCoinWatchlist(UUID watchlistCoinId, CoinWatchlistRequest request) {
        WatchlistCoin watchlistCoin = watchlistCoinRepository.findById(watchlistCoinId).orElseThrow(IllegalArgumentException::new);
        watchlistCoin.updateLimitPrice(request);

        return CoinWatchlistResponseDto.fromEntity(watchlistCoin);
    }

    @Transactional
    public void deleteCoinWatchlist(UUID watchlistCoinId) {
        if (!watchlistCoinRepository.existsById(watchlistCoinId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        watchlistCoinRepository.deleteById(watchlistCoinId);
    }

}
