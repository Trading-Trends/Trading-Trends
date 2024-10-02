package com.tradingtrends.batch.domain.model.Entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "documents", writeTypeHint = WriteTypeHint.FALSE)
@Setting(settingPath = "elastic/document-setting.json")
@Mapping(mappingPath = "elastic/document-mapping.json")
public class DisclosureDocument {

    @Id
    private String documentId;  // UUID 혹은 고유 문서 식별자

    @Nullable @Field(type = FieldType.Text)
    private String documentName;  // 분기보고서 등의 문서명

    @Nullable @Field(type = FieldType.Text)
    private String formulaVersion;  // 4.8 같은 서식 버전

    @Nullable @Field(type = FieldType.Text)
    private String companyName;  // 회사명

    @Nullable @Field(type = FieldType.Text)
    private String registrationNumber;  // 회사 등록번호

    @Nullable @Field(type = FieldType.Text)
    private String summary;  // 요약 정보

    @Nullable @Field(type = FieldType.Date)
    private String reportDate;  // 보고일 (날짜)

    @Nullable @Field(type = FieldType.Object)
    private List<Correction> corrections;  // 정정 사항들

    @Nullable @Field(type = FieldType.Object)
    private List<Library> libraries;  // 문서 라이브러리들

    @Builder
    public DisclosureDocument(String documentId, String documentName, String formulaVersion, String companyName,
                              String registrationNumber, String summary, String reportDate, List<Correction> corrections,
                              List<Library> libraries) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.formulaVersion = formulaVersion;
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.summary = summary;
        this.reportDate = reportDate;
        this.corrections = corrections;
        this.libraries = libraries;
    }

    // 정정 사항을 나타내는 내부 클래스
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Correction {
        @Nullable @Field(type = FieldType.Text)
        private String correctionDetails;  // 정정 사항 내용
        @Nullable @Field(type = FieldType.Date)
        private String correctionDate;  // 정정 일자

        @Builder
        public Correction(String correctionDetails, String correctionDate) {
            this.correctionDetails = correctionDetails;
            this.correctionDate = correctionDate;
        }
    }

    // 문서 라이브러리 정보 클래스
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Library {
        @Nullable @Field(type = FieldType.Text)
        private String title;  // 라이브러리 제목
        @Nullable @Field(type = FieldType.Text)
        private String content;  // 라이브러리 내용 (표, 문단 등)
        @Nullable @Field(type = FieldType.Object)
        private List<Table> tables;  // 테이블 정보

        @Builder
        public Library(String title, String content, List<Table> tables) {
            this.title = title;
            this.content = content;
            this.tables = tables;
        }
    }

    // 테이블 정보 클래스
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Table {
        @Nullable @Field(type = FieldType.Text)
        private String tableClass;  // 테이블 클래스 (NORMAL 등)
        @Nullable @Field(type = FieldType.Boolean)
        private Boolean isFixedTable;  // 고정 테이블 여부
        @Nullable @Field(type = FieldType.Integer)
        private Integer width;  // 테이블 너비
        @Nullable @Field(type = FieldType.Object)
        private List<Row> rows;  // 테이블 행들

        @Builder
        public Table(String tableClass, Boolean isFixedTable, Integer width, List<Row> rows) {
            this.tableClass = tableClass;
            this.isFixedTable = isFixedTable;
            this.width = width;
            this.rows = rows;
        }
    }

    // 테이블 행 (Row) 정보
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Row {
        @Nullable @Field(type = FieldType.Boolean)
        private Boolean isCopyable;  // 복사 가능 여부
        @Nullable @Field(type = FieldType.Boolean)
        private Boolean isDeletable;  // 삭제 가능 여부
        @Nullable @Field(type = FieldType.Object)
        private List<Cell> cells;  // 셀 정보

        @Builder
        public Row(Boolean isCopyable, Boolean isDeletable, List<Cell> cells) {
            this.isCopyable = isCopyable;
            this.isDeletable = isDeletable;
            this.cells = cells;
        }
    }

    // 셀 (Cell) 정보
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Cell {
        @Nullable @Field(type = FieldType.Integer)
        private Integer width;  // 셀 너비
        @Nullable @Field(type = FieldType.Integer)
        private Integer height;  // 셀 높이
        @Nullable @Field(type = FieldType.Text)
        private String alignment;  // 정렬
        @Nullable @Field(type = FieldType.Text)
        private String content;  // 셀 내용

        @Builder
        public Cell(Integer width, Integer height, String alignment, String content) {
            this.width = width;
            this.height = height;
            this.alignment = alignment;
            this.content = content;
        }
    }
}

