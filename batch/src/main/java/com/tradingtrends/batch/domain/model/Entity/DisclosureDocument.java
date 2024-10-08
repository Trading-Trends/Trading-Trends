package com.tradingtrends.batch.domain.model.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(indexName = "disclosures")
@Setting(settingPath = "elastic/document-setting.json")
@Mapping(mappingPath = "elastic/document-mapping.json")
public class DisclosureDocument {

    @Id
    private String rceptNo;

    @Field(type = FieldType.Text)
    private String corpName;

    @Field(type = FieldType.Text)
    private String corpCode;

    @Field(type = FieldType.Text)
    private String reportNm;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private String rceptDt;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private String loadDt;

    @Field(type = FieldType.Text)
    private String rawXmlData;  // XML 원본 데이터를 저장
}
