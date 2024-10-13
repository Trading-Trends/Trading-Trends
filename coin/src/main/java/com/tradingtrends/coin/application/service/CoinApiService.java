package com.tradingtrends.coin.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingtrends.coin.application.dto.CoinApiResponseDto;
import com.tradingtrends.coin.application.dto.CoinApiSnapshotResponseDto;
import com.tradingtrends.coin.domain.model.CoinInfo;
import com.tradingtrends.coin.domain.repository.CoinRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinApiService {

    private final CoinRepository coinRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public List<CoinApiResponseDto> fetchAndSaveCoinMarketInfoFromOpenAPI() {
        String url = "https://api.upbit.com/v1/market/all?isDetails=true";

        // API 호출 및 응답 처리
        ResponseEntity<CoinApiResponseDto[]> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            CoinApiResponseDto[].class
        );

        CoinApiResponseDto[] coinDtos = response.getBody();
        List<CoinApiResponseDto> coinDtoList = new ArrayList<>(Arrays.asList(coinDtos));

        // 응답 데이터 저장 및 변환
        coinDtoList.stream()
            .map(CoinInfo::createCoin)
            .forEach(coinRepository::save);

        // DTO 리스트 반환
        return coinDtoList;
    }

    public List<CoinApiSnapshotResponseDto> fetchAndCacheCoinMarketSnapshotFromOpenAPI() {
        String url = "https://api.upbit.com/v1/ticker/all?quoteCurrencies=KRW";

        /**
         * "market" : "KRW-BTC"
         * "trade_price": 82324000.00000000,
         * "signed_change_price": -576000.00000000,
         * "signed_change_rate": -0.0069481303,
         */
        // API 호출
        ResponseEntity<CoinApiSnapshotResponseDto[]> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            CoinApiSnapshotResponseDto[].class
        );

        // 응답 처리
        CoinApiSnapshotResponseDto[] coins = response.getBody();
        if (coins == null) {
            return Collections.emptyList();
        }

        // 필요한 정보만 필터링하여 리스트로 변환
        List<CoinApiSnapshotResponseDto> filteredCoins = Arrays.stream(coins)
            .filter(coin -> coin.getMarket().startsWith("KRW"))
            .collect(Collectors.toList());

        // Redis에 마켓 단위 현재가 정보 데이터 저장
        filteredCoins.forEach(coin -> {
            String redisKey = "coin:" + coin.getMarket();
            redisTemplate.opsForHash()
                .put(redisKey, "signedChangePrice", coin.getSignedChangePrice());
            redisTemplate.opsForHash()
                .put(redisKey, "signedChangeRate", coin.getSignedChangeRate());
            redisTemplate.opsForHash().put(redisKey, "tradePrice", coin.getTradePrice());
        });

        // signedChangeRate 기준으로 상위 5개, 하위 5개 정렬
        List<CoinApiSnapshotResponseDto> sortedByRate = filteredCoins.stream()
            .sorted(
                Comparator.comparing(CoinApiSnapshotResponseDto::getSignedChangeRate).reversed())
            .collect(Collectors.toList());

        // 상위 5개 데이터 캐시 (전날 대비 상승폭이 큰 코인)
        List<CoinApiSnapshotResponseDto> bestCoins = new ArrayList<>(sortedByRate.subList(0,
            Math.min(5, sortedByRate.size())));
        redisTemplate.opsForValue().set("bestCoinsFromPrevDay", bestCoins);

        // 하위 5개 데이터 캐시 (전날 대비 하락폭이 큰 코인)
        List<CoinApiSnapshotResponseDto> worstCoins = new ArrayList<>(sortedByRate.subList(
            Math.max(0, sortedByRate.size() - 5), sortedByRate.size()));
        redisTemplate.opsForValue().set("worstCoinsFromPrevDay", worstCoins);

        // 필터링된 리스트 반환
        return filteredCoins;
    }

    @SuppressWarnings("unchecked")
    public List<CoinApiSnapshotResponseDto> getBestCoins() {
        Object result = redisTemplate.opsForValue().get("bestCoinsFromPrevDay");
        if (result instanceof List<?>) {
            return (List<CoinApiSnapshotResponseDto>) result;
        } else {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<CoinApiSnapshotResponseDto> getWorstCoins() {
        Object result = redisTemplate.opsForValue().get("worstCoinsFromPrevDay");
        if (result instanceof List<?>) {
            return (List<CoinApiSnapshotResponseDto>) result;
        } else {
            return Collections.emptyList();
        }
    }

    public CoinApiResponseDto getCoinMarketInfo(UUID coinId) {
        CoinInfo coinInfo = coinRepository.findById(coinId)
            .orElseThrow(IllegalArgumentException::new);
        return CoinApiResponseDto.fromEntity(coinInfo);
    }

    public Page<CoinApiResponseDto> getCoinMarketInfos(Pageable pageable) {
        Page<CoinInfo> coinInfos = coinRepository.findAll(pageable);
        return coinInfos.map(CoinApiResponseDto::fromEntity);
    }
}
