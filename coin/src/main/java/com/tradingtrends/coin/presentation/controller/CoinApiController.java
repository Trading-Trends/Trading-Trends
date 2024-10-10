package com.tradingtrends.coin.presentation.controller;

import com.tradingtrends.coin.application.dto.CoinApiResponseDto;
import com.tradingtrends.coin.application.dto.CoinApiSnapshotResponseDto;
import com.tradingtrends.coin.application.service.CoinApiService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/coin")
public class CoinApiController {

    private final CoinApiService coinApiService;

    /**
     * 종목별 상세 정보 open api 이용 및 DB 저장
     * @return
     */
    @GetMapping("/market-info")
    public ResponseEntity<List<CoinApiResponseDto>> fetchAndSaveCoinMarketInfoFromOpenAPI(){
        return ResponseEntity.status(HttpStatus.OK).body(coinApiService.fetchAndSaveCoinMarketInfoFromOpenAPI());
    }



    /**
     * 마켓 단위 종목들의 스냅샷 반환 open api 이용
     * 종목별 마켓 단위 현재가 정보 캐싱 처리 - 등락률 BEST,WORST 기능 구현에 필요, 10분 단위 캐싱(자동화 필요 할듯)
     * 전일 종가 기준으로 signed_change_rate 로 BEST, WORST 기능 구현
     * =============================================================
     * BEST, WORST 기능을 캐싱된 전체 데이터에서 REDIS 에서 조회 후
     * 반환하는 API 를 만들게 되면 BEST, WORST 조회가 안될시 OPEN API를 호출하는 방법 이 있지만
     * 테스트의 편의상 한번에 진행
     */
    @GetMapping("/market-current-info/snapshot")
    public ResponseEntity<List<CoinApiSnapshotResponseDto>> fetchAndCacheCoinMarketSnapshotFromOpenAPI(){
        return ResponseEntity.status(HttpStatus.OK).body(coinApiService.fetchAndCacheCoinMarketSnapshotFromOpenAPI());
    }

    /**
     * 전날 대비 상승폭이 큰 코인 (best) 조회
     */
    @GetMapping("/fluctuation-rate/best")
    public ResponseEntity<List<CoinApiSnapshotResponseDto>> getBestCoins() {
        return ResponseEntity.status(HttpStatus.OK).body(coinApiService.getBestCoins());
    }

    /**
     * 전날 대비 하락폭이 큰 코인 (worst) 조회
     */
    @GetMapping("/fluctuation-rate/worst")
    public ResponseEntity<List<CoinApiSnapshotResponseDto>> getWorstCoins() {
        return ResponseEntity.status(HttpStatus.OK).body(coinApiService.getWorstCoins());
    }

    /**
     * 코인 종목 상세 조회 (단건 조회)
     */
    @GetMapping("/market-info/{coin_id}")
    public ResponseEntity<CoinApiResponseDto> getCoinMarketInfo(
        @PathVariable("coin_id") UUID coinId)
    {
        return ResponseEntity.status(HttpStatus.OK).body(coinApiService.getCoinMarketInfo(coinId));
    }


    /**
     * 코인 종목 다건 조회 (페이징 조회)
     *         @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.ASC) Pageable pageable
     */
    @GetMapping
    public ResponseEntity<Page<CoinApiResponseDto>> getCoinMarketInfos(
        @PageableDefault(page = 0, size = 10, direction = Direction.ASC) Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK).body(coinApiService.getCoinMarketInfos(pageable));
    }

}
