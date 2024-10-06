package com.tradingtrends.batch.domain.model.Entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(indexName = "disclosures")
@Setting(settingPath = "elastic/document-setting.json")
@Mapping(mappingPath = "elastic/document-mapping.json")
public class DisclosureDocument {

    @Id
    @JacksonXmlProperty(localName = "DOCUMENT-ID")
    private String documentId;

    @JacksonXmlProperty(localName = "DOCUMENT-NAME")
    private DocumentNameElement documentName;

    @JacksonXmlProperty(localName = "FORMULA-VERSION")
    private FormulaVersion formulaVersion;

    @JacksonXmlProperty(localName = "COMPANY-NAME")
    private CompanyName companyName;

    @JacksonXmlElementWrapper(localName = "SUMMARY", useWrapping = false)
    @JacksonXmlProperty(localName = "EXTRACTION")
    private List<Extraction> extractions;

    @JacksonXmlElementWrapper(localName = "BODY", useWrapping = false)
    @JacksonXmlProperty(localName = "LIBRARY")
    private List<LibraryElement> libraries;

    // 내부 클래스 정의
    @Getter
    @Setter
    @NoArgsConstructor
    public static class DocumentNameElement {
        @JacksonXmlProperty(isAttribute = true, localName = "ACODE")
        private String acode;

        @JacksonXmlText
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FormulaVersion {
        @JacksonXmlProperty(isAttribute = true, localName = "ADATE")
        private String adate;

        @JacksonXmlText
        private String version;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CompanyName {
        @JacksonXmlProperty(isAttribute = true, localName = "AREGCIK")
        private String regcik;

        @JacksonXmlText
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Extraction {
        @JacksonXmlProperty(isAttribute = true, localName = "ACODE")
        private String acode;

        @JacksonXmlProperty(isAttribute = true, localName = "AFEATURE")
        private String feature;

        @JacksonXmlText
        private String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LibraryElement {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "TITLE")
        private List<TitleElement> titles;

        @JacksonXmlElementWrapper(localName = "TABLE", useWrapping = false)
        private List<Table> tables;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TitleElement {
        @JacksonXmlProperty(isAttribute = true, localName = "ATOC")
        private String atoc;

        @JacksonXmlProperty(isAttribute = true, localName = "AASSOCNOTE")
        private String aassocnote;

        @JacksonXmlText
        private String titleText;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Table {
        @JacksonXmlElementWrapper(localName = "TR", useWrapping = false)
        @JacksonXmlProperty(localName = "TD")
        private List<TableRow> rows;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TableRow {
        @JacksonXmlElementWrapper(localName = "TD", useWrapping = false)
        private List<TableCell> cells;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TableCell {
        @JacksonXmlProperty(isAttribute = true, localName = "WIDTH")
        private String width;

        @JacksonXmlText
        private String content;
    }
}
