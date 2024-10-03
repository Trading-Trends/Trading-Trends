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

    @Field(type = FieldType.Text)
    @JacksonXmlProperty(localName = "DOCUMENT-NAME")
    private String documentName;

    @Field(type = FieldType.Text)
    @JacksonXmlProperty(localName = "FORMULA-VERSION")
    private String formulaVersion;

    @Field(type = FieldType.Text)
    @JacksonXmlProperty(localName = "COMPANY-NAME")
    private String companyName;

    @Field(type = FieldType.Nested)
    @JacksonXmlElementWrapper(localName = "BODY")
    @JacksonXmlProperty(localName = "LIBRARY")
    private List<Library> libraries;

    // 내부 클래스들...
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Library {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "SECTION-1")
        private List<Section> sections;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Section {

        @JacksonXmlProperty(localName = "TITLE")
        private Title title;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "TABLE-GROUP")
        private List<TableGroup> tableGroups;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "P")
        private List<String> paragraphs;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Title {

        @JacksonXmlProperty(localName = "ATOC")
        private String atoc;

        @JacksonXmlProperty(localName = "AASSOCNOTE")
        private String aassocnote;

        @JacksonXmlText
        private String titleText;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class TableGroup {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "TABLE")
        private List<Table> tables;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Table {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "COLGROUP")
        private List<ColGroup> colGroups;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "TBODY")
        private List<TableBody> tableBodies;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class ColGroup {

        @JacksonXmlProperty(localName = "COL")
        private List<Col> cols;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Col {

        @JacksonXmlProperty(localName = "WIDTH")
        private String width;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class TableBody {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "TR")
        private List<TableRow> rows;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class TableRow {

        @JacksonXmlProperty(localName = "TD")
        private List<TableCell> cells;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class TableCell {

        @JacksonXmlProperty(localName = "WIDTH")
        private String width;

        @JacksonXmlText
        private String content;
    }
}
