package com.tradingtrends.batch.application.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.tradingtrends.batch.domain.model.Entity.DisclosureDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.apache.commons.text.StringEscapeUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.xml.sax.SAXException;

@Slf4j
@Service
public class DartToElasticsearchService {

    @Value("${dart.api-key}")
    private String apiKey;

    @Value("${dart.document-url}")
    private String documentUrl;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public void fetchDocumentAndSaveToElasticsearch(String rceptNo) throws IOException {
        String url = documentUrl + "?crtfc_key=" + apiKey + "&rcept_no=" + rceptNo +"&reprt_code=" ;
        log.info("[문서 패치] Fetching dart document from " + url);

        // 원본 zip 파일 요청 및 다운로드
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
        log.info("Fetched document response size: " + response.getBody().length);

        // 압축 해제
        File tempFile = Files.createTempFile("dart", ".zip").toFile();
        Files.write(tempFile.toPath(), Objects.requireNonNull(response.getBody()));
        ZipFile zipFile = new ZipFile(tempFile);
        ZipArchiveEntry entry = zipFile.getEntries().asIterator().next(); // 첫 번째 파일

        Path extractedPath = Files.createTempFile("extracted", ".xml");
        try (InputStream zipInputStream = zipFile.getInputStream(entry)) {
            Files.copy(zipInputStream, extractedPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // XML 파일을 DisclosureDocument로 변환
        DisclosureDocument disclosureDocument = parseXmlToDisclosureDocument(extractedPath);

        // Elasticsearch에 저장
        IndexRequest<DisclosureDocument> indexRequest = new IndexRequest.Builder<DisclosureDocument>()
                .index("disclosures")
                .id(rceptNo)
                .document(disclosureDocument)
                .build();

        IndexResponse responseES = elasticsearchClient.index(indexRequest);
        log.info("Document indexed with ID: " + responseES.id());
    }

    private DisclosureDocument parseXmlToDisclosureDocument(Path xmlFilePath) {
        try {


            // XML 파서 설정
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // XML 파일을 문자열로 읽기
            String xmlContent = new String(Files.readAllBytes(xmlFilePath));

            // XML의 태그 안에 있는 텍스트는 보존하고, 외부의 특수문자만 이스케이프 처리
            String escapedContent = escapeSpecialCharacters(xmlContent);

            log.info("Processed XML content: " + escapedContent.substring(0, 1500)); // 예시로 1500글자 출력

            // 이스케이프 처리한 XML 내용을 Document로 파싱
            org.w3c.dom.Document xmlDocument = builder.parse(new InputSource(new StringReader(escapedContent)));


            // XPath 설정
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            // DisclosureDocument 생성 및 필드 매핑
            DisclosureDocument.DisclosureDocumentBuilder documentBuilder = DisclosureDocument.builder()
                    .documentId(getXPathValue(xPath, xmlDocument, "/Document/DocumentId"))
                    .documentName(getXPathValue(xPath, xmlDocument, "/Document/DocumentName"))
                    .formulaVersion(getXPathValue(xPath, xmlDocument, "/Document/FormulaVersion"))
                    .companyName(getXPathValue(xPath, xmlDocument, "/Document/CompanyName"))
                    .registrationNumber(getXPathValue(xPath, xmlDocument, "/Document/RegistrationNumber"))
                    .summary(getXPathValue(xPath, xmlDocument, "/Document/Summary"))
                    .reportDate(getXPathValue(xPath, xmlDocument, "/Document/ReportDate"));

            // 정정 사항
            List<DisclosureDocument.Correction> corrections = new ArrayList<>();
            org.w3c.dom.NodeList correctionNodes = (org.w3c.dom.NodeList) xPath.evaluate("/Document/Corrections/Correction", xmlDocument, XPathConstants.NODESET);
            for (int i = 0; i < correctionNodes.getLength(); i++) {
                org.w3c.dom.Node node = correctionNodes.item(i);
                corrections.add(DisclosureDocument.Correction.builder()
                        .correctionDetails(getXPathValue(xPath, node, "CorrectionDetails"))
                        .correctionDate(getXPathValue(xPath, node, "CorrectionDate"))
                        .build());
            }
            documentBuilder.corrections(corrections);

            // 라이브러리
            List<DisclosureDocument.Library> libraries = new ArrayList<>();
            org.w3c.dom.NodeList libraryNodes = (org.w3c.dom.NodeList) xPath.evaluate("/Document/Libraries/Library", xmlDocument, XPathConstants.NODESET);
            for (int i = 0; i < libraryNodes.getLength(); i++) {
                org.w3c.dom.Node node = libraryNodes.item(i);
                List<DisclosureDocument.Table> tables = new ArrayList<>();
                org.w3c.dom.NodeList tableNodes = (org.w3c.dom.NodeList) xPath.evaluate("Tables/Table", node, XPathConstants.NODESET);
                for (int j = 0; j < tableNodes.getLength(); j++) {
                    org.w3c.dom.Node tableNode = tableNodes.item(j);
                    List<DisclosureDocument.Row> rows = new ArrayList<>();
                    org.w3c.dom.NodeList rowNodes = (org.w3c.dom.NodeList) xPath.evaluate("Rows/Row", tableNode, XPathConstants.NODESET);
                    for (int k = 0; k < rowNodes.getLength(); k++) {
                        org.w3c.dom.Node rowNode = rowNodes.item(k);
                        List<DisclosureDocument.Cell> cells = new ArrayList<>();
                        org.w3c.dom.NodeList cellNodes = (org.w3c.dom.NodeList) xPath.evaluate("Cells/Cell", rowNode, XPathConstants.NODESET);
                        for (int l = 0; l < cellNodes.getLength(); l++) {
                            org.w3c.dom.Node cellNode = cellNodes.item(l);
                            cells.add(DisclosureDocument.Cell.builder()
                                    .width(Integer.parseInt(getXPathValue(xPath, cellNode, "Width")))
                                    .height(Integer.parseInt(getXPathValue(xPath, cellNode, "Height")))
                                    .alignment(getXPathValue(xPath, cellNode, "Alignment"))
                                    .content(getXPathValue(xPath, cellNode, "Content"))
                                    .build());
                        }
                        rows.add(DisclosureDocument.Row.builder()
                                .isCopyable(Boolean.parseBoolean(getXPathValue(xPath, rowNode, "IsCopyable")))
                                .isDeletable(Boolean.parseBoolean(getXPathValue(xPath, rowNode, "IsDeletable")))
                                .cells(cells)
                                .build());
                    }
                    tables.add(DisclosureDocument.Table.builder()
                            .tableClass(getXPathValue(xPath, tableNode, "TableClass"))
                            .isFixedTable(Boolean.parseBoolean(getXPathValue(xPath, tableNode, "IsFixedTable")))
                            .width(Integer.parseInt(getXPathValue(xPath, tableNode, "Width")))
                            .rows(rows)
                            .build());
                }
                libraries.add(DisclosureDocument.Library.builder()
                        .title(getXPathValue(xPath, node, "Title"))
                        .content(getXPathValue(xPath, node, "Content"))
                        .tables(tables)
                        .build());
            }
            documentBuilder.libraries(libraries);

            // DisclosureDocument 반환
            return documentBuilder.build();

        } catch (Exception e) {
            log.error("Error parsing XML to DisclosureDocument", e);
            return null;
        }
    }

    private boolean isValidXml(Path xmlFilePath) {
        try {
            // XML 파서 설정
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(xmlFilePath.toFile());
            return true;
        } catch (SAXException | IOException | ParserConfigurationException e) {
            log.error("Invalid XML format for file: " + xmlFilePath, e);
            return false;
        }
    }

    private String getXPathValue(XPath xPath, org.w3c.dom.Node node, String expression) {
        try {
            return xPath.evaluate(expression, node);
        } catch (Exception e) {
            log.error("Error evaluating XPath expression: " + expression, e);
            return null;
        }
    }

    public String escapeSpecialCharacters(String htmlContent) {
        StringBuilder result = new StringBuilder();
        boolean insideTag = false;

        for (char c : htmlContent.toCharArray()) {
            if (c == '<') {
                insideTag = true;  // 태그의 시작
                result.append(c);  // '<' 추가
            } else if (c == '>') {
                insideTag = false; // 태그의 끝
                result.append(c);  // '>' 추가
            } else if (insideTag) {
                // 태그 내부
                result.append(c); // 일반 문자 추가

            } else {
                // 태그 외부에서의 특수문자 변환
                if (c == '&') {
                    result.append("&amp;");
                } else if (c == '<') {
                    result.append("&lt;");
                } else if (c == '>') {
                    result.append("&gt;");
                } else if (c == '"') {
                    result.append("&quot;");
                } else if (c == '\'') {
                    result.append("&apos;");
                } else {
                    result.append(c); // 일반 문자 추가
                }
            }
        }

        return result.toString();
    }




    private String escapeXmlSpecialCharacters(String xml) {
        if (xml == null) return null;
        return xml.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }


}
