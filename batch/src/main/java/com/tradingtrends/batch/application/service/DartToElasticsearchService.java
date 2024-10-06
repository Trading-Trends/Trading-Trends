package com.tradingtrends.batch.application.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tradingtrends.batch.domain.model.Entity.DisclosureDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DartToElasticsearchService {

    @Value("${dart.api-key}")
    private String apiKey;

    @Value("${dart.document-url}")
    private String documentUrl;

    private final ElasticsearchClient elasticsearchClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public void fetchDocumentAndSaveToElasticsearch(String rceptNo) {
        String url = String.format("%s?crtfc_key=%s&rcept_no=%s", documentUrl, apiKey, rceptNo);
        log.info("[문서 패치] Fetching DART document from {}", url);

        try {
            byte[] zipData = downloadZipFile(url);
            File xmlFile = extractXmlFromZip(zipData);
            DisclosureDocument document = parseXmlToDisclosureDocument(xmlFile);
            indexDocumentToElasticsearch(rceptNo, document);
            log.info("Data ingestion completed successfully for rceptNo: {}", rceptNo);
        } catch (Exception e) {
            log.error("Error processing rceptNo {}: {}", rceptNo, e.getMessage(), e);
        }
    }

    private byte[] downloadZipFile(String url) throws IOException {
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
        if (response.getBody() == null || response.getBody().length == 0) {
            throw new IOException("Received empty response from DART API");
        }
        log.info("Downloaded ZIP file size: {} bytes", response.getBody().length);
        return response.getBody();
    }

    private File extractXmlFromZip(byte[] zipData) throws IOException {
        File tempZipFile = Files.createTempFile("dart_", ".zip").toFile();
        Files.write(tempZipFile.toPath(), zipData);

        try (ZipFile zipFile = new ZipFile(tempZipFile)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".xml")) {
                    File tempXmlFile = Files.createTempFile("dart_xml_", ".xml").toFile();
                    try (InputStream is = zipFile.getInputStream(entry);
                         OutputStream os = new FileOutputStream(tempXmlFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    }
                    log.info("Extracted XML file: {}", tempXmlFile.getAbsolutePath());
                    return tempXmlFile;
                }
            }
            throw new FileNotFoundException("No XML file found in the ZIP archive");
        } finally {
            if (!tempZipFile.delete()) {
                log.warn("Failed to delete temporary ZIP file: {}", tempZipFile.getAbsolutePath());
            }
        }
    }

    private DisclosureDocument parseXmlToDisclosureDocument(File xmlFile) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 파일을 문자열로 읽기
        String xmlContent;
        try (InputStream inputStream = new FileInputStream(xmlFile)) {
            xmlContent = new String(inputStream.readAllBytes());
        }

        log.info("원본 XML 내용: \n{}", xmlContent.substring(0, Math.min(xmlContent.length(), 500)));

        // 태그 내부 텍스트만 이스케이프 처리
        xmlContent = XmlEscapeUtil.escapeXmlInsideTags(xmlContent);

        log.info("이스케이프된 XML 내용: \n{}", xmlContent.substring(0, Math.min(xmlContent.length(), 500)));

        // 처리된 XML 문자열을 파싱
            DisclosureDocument document = xmlMapper.readValue(xmlContent, DisclosureDocument.class);
            log.info("Parsed DisclosureDocument: {}", document.getDocumentName());
            return document;
    }

    private void indexDocumentToElasticsearch(String rceptNo, DisclosureDocument document) throws IOException {
        IndexRequest<DisclosureDocument> indexRequest = IndexRequest.of(i -> i
                .index("disclosures")
                .id(rceptNo)
                .document(document)
        );
        IndexResponse response = elasticsearchClient.index(indexRequest);
        log.info("Indexed document with ID: {}", response.id());
    }

    public static class XmlEscapeUtil {

        // 진짜 태그 형식에 맞지 않는 가짜 태그 패턴
        private static final Pattern PSEUDO_TAG_PATTERN = Pattern.compile("<([가-힣][^<>]*?)>");
        // 태그 안의 텍스트만 처리하는 패턴
        private static final Pattern TAG_CONTENT_PATTERN = Pattern.compile(">([^<]+)<");

        public static String escapeXmlInsideTags(String input) {
            if (input == null) {
                return null;
            }

            // 먼저 가짜 태그를 감지하고 이를 이스케이프 처리
            input = escapePseudoTags(input);

            Matcher matcher = TAG_CONTENT_PATTERN.matcher(input);
            StringBuilder escapedXml = new StringBuilder();

            // 태그 내부 텍스트만 이스케이프 처리
            while (matcher.find()) {
                String contentInsideTag = matcher.group(1);
                String escapedContent = escapeXml(contentInsideTag);

                matcher.appendReplacement(escapedXml, ">" + Matcher.quoteReplacement(escapedContent) + "<");
            }

            matcher.appendTail(escapedXml);
            return escapedXml.toString();
        }

        // 가짜 태그를 이스케이프 처리
        public static String escapePseudoTags(String input) {
            Matcher matcher = PSEUDO_TAG_PATTERN.matcher(input);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String pseudoTag = matcher.group(0);  // 가짜 태그 전체를 찾음
                String escapedTag = pseudoTag.replace("<", "&lt;").replace(">", "&gt;");
                matcher.appendReplacement(result, Matcher.quoteReplacement(escapedTag));
            }

            matcher.appendTail(result);
            return result.toString();
        }

        // XML 특수 문자를 이스케이프 처리
        public static String escapeXml(String input) {
            if (input == null) {
                return null;
            }
            return input
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
        }
    }
}