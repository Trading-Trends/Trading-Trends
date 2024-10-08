package com.tradingtrends.batch.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tradingtrends.batch.application.dto.CorporateCodesResponseDto;
import com.tradingtrends.batch.application.dto.DartCorporateFinanceApiResponse;
import com.tradingtrends.batch.domain.model.CorporateCodes;
import com.tradingtrends.batch.domain.model.CorporateMajorFinance;
import com.tradingtrends.batch.domain.repository.CorporateFinanceRepository;
import com.tradingtrends.batch.domain.repository.CorporatesCodesRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
@Slf4j
public class CorporateService {

    private final CorporatesCodesRepository corporatesCodesRepository;
    private final CorporateFinanceRepository corporateFinanceRepository;
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    @Value("${dart.api-key}")
    private String DART_API_KEY;

    @Value("${dart.corp-code-url}")
    private String DART_CORP_CODE_URL;

    @Value("${dart.corp-finance-url}")
    private String DART_CORP_FINANCE_URL;

    public List<CorporateCodesResponseDto> fetchAndSaveCorpCodeInfo() throws Exception {

        String url = DART_CORP_CODE_URL + DART_API_KEY;
        log.info("API 요청 URL: {}", url);
        List<CorporateCodesResponseDto> corpInfoDtoList = new ArrayList<>();

        // DB에서 이미 저장된 고유번호를 조회
        Set<String> existingCorpCodes = new HashSet<>(corporatesCodesRepository.findAllCorpCodes());

        // DART API에서 ZIP 파일을 다운로드
        byte[] zipBytes = restTemplate.getForObject(url, byte[].class);
        log.info("ZIP 파일 크기: {} bytes", zipBytes.length);

        // ZIP 파일 해제 및 XML 파일 추출
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes), StandardCharsets.UTF_8);
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            log.info("처리 중인 파일: {}", zipEntry.getName());

            if (zipEntry.getName().endsWith(".xml")) {
                // XML 파일을 임시 파일로 저장
                Path tempFile = Files.createTempFile("dart_corp_info", ".xml");
                Files.copy(zipInputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                log.info("XML 파일이 임시 저장소에 저장됨: {}", tempFile.toAbsolutePath());

                // 임시 파일 내용 콘솔에 출력 (XML 파일 내용 출력)
//                List<String> xmlContent = Files.readAllLines(tempFile);
//                log.info("XML 파일 내용:");
//                for (String line : xmlContent) {
//                    log.info(line);
//                }

                zipInputStream.closeEntry();

                // XML 파일을 JSON으로 변환
                InputStream xmlInputStream = Files.newInputStream(tempFile);
                JsonNode rootNode = xmlMapper.readTree(xmlInputStream);  // XmlMapper 사용

                JsonNode corpList = rootNode.path("list");
                log.info("총 기업 수: {}", corpList.size());

                // JSON 데이터를 파싱하여 CorpInfo 엔티티에 저장하고, DTO로 변환
                for (JsonNode corpNode : corpList) {

                    String corpCode = corpNode.path("corp_code").asText();

                    // 이미 DB에 존재하는 경우, 저장하지 않음
                    if (!existingCorpCodes.contains(corpCode)) {
                        String corpName = corpNode.path("corp_name").asText();
                        String stockCode = corpNode.path("stock_code").asText();
                        String modifyDate = corpNode.path("modify_date").asText();

                        CorporateCodes corpInfo = new CorporateCodes(corpCode, corpName, stockCode, modifyDate, false);
                        corporatesCodesRepository.save(corpInfo);  // 새로운 데이터만 DB에 저장
                        log.info("기업 정보가 DB에 저장됨: {}", corpInfo.getCorpName());

                        // DTO로 변환하여 리스트에 추가
                        CorporateCodesResponseDto corpInfoDto = new CorporateCodesResponseDto(
                            corpInfo.getCorpCode(),
                            corpInfo.getCorpName(),
                            corpInfo.getStockCode(),
                            corpInfo.getModifyDate()
                        );
                        corpInfoDtoList.add(corpInfoDto);
                    } else {
                        log.info("이미 저장된 기업 정보: corpCode={}", corpCode);
                    }
                }

                Files.delete(tempFile);  // 임시 파일 삭제
            }

            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();

        // DTO 리스트 반환
        return corpInfoDtoList;
    }

    @Transactional
    public List<DartCorporateFinanceApiResponse.CorporateFinanceDto> fetchAndSaveCorpFinanceInfo() throws Exception {
        List<DartCorporateFinanceApiResponse.CorporateFinanceDto> financeResponseDtoList = new ArrayList<>();

        // 1분에 100번의 api 호출 제한이 있기 때문에 50번씩으로 제한 둠
        Pageable pageable = PageRequest.of(0, 50);

        // DB에서 stockCode 가 존재하는 corporateCodes entity 의 corpCodes 필드 리스트를 조회
        List<String> corpCodes = corporatesCodesRepository.findAllCorpCodesAndStockCodeIsNotNullInYear(pageable);
        log.info("총 기업 수: {}", corpCodes.size());

        // 가져온 corpCodes에 해당하는 CorporateCodes 엔티티들을 조회
        List<CorporateCodes> corporateCodesList = corporatesCodesRepository.findByCorpCodeIn(corpCodes);

        // 각각의 CorporateCodes 객체에서 isStockCodeChecked 값을 true로 변경
        corporateCodesList.forEach(CorporateCodes::markStockCodeChecked);

        for (String corpCode : corpCodes) {
            log.info("Processing financial data for corpCode: {}", corpCode);

            // DART API 호출 URL 구성

            String url = UriComponentsBuilder.fromHttpUrl(DART_CORP_FINANCE_URL)
                .queryParam("crtfc_key", DART_API_KEY)
                .queryParam("corp_code", corpCode)
                .queryParam("bsns_year", "2023")
                .queryParam("reprt_code", "11014")
                .queryParam("idx_cl_code", "M210000")
                .toUriString();

            // DART API 호출
            ResponseEntity<DartCorporateFinanceApiResponse> response = restTemplate.getForEntity(url, DartCorporateFinanceApiResponse.class);
            DartCorporateFinanceApiResponse dartApiResponse = response.getBody();
            if (dartApiResponse != null && dartApiResponse.getStatus().equals("000")) {
                List<DartCorporateFinanceApiResponse.CorporateFinanceDto> financeDataList = dartApiResponse.getList();

                if (financeDataList != null) {
                    for (DartCorporateFinanceApiResponse.CorporateFinanceDto dto : financeDataList) {
                        Optional<CorporateMajorFinance> existingFinance = corporateFinanceRepository.findByCorpCodeAndBsnsYearAndReprtCodeAndIdxNm(
                            dto.getCorp_code(), dto.getBsns_year(), dto.getReprt_code(), dto.getIdx_nm());

                        // 데이터가 존재하지 않을 경우 저장
                        if (!existingFinance.isPresent()) {
                            corporateFinanceRepository.save(new CorporateMajorFinance(dto));
                            financeResponseDtoList.add(dto);
                        } else {
                            log.info("Financial data for corpCode={} and year={} already exists", dto.getCorp_code(), dto.getBsns_year());
                        }
                    }
                } else {
                    log.warn("No financial data found for corpCode: {}", corpCode);
                }
            } else {
                log.warn("Error fetching data for corpCode={}, status={}, message={}", corpCode, dartApiResponse.getStatus(), dartApiResponse.getMessage());
            }
        }
        // DTO 리스트 반환
        return financeResponseDtoList;
    }

}

