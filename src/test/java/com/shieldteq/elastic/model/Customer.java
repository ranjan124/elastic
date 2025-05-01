package com.shieldteq.elastic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "customers")
@Setting(settingPath = "index/index-setting.json")
@Mapping(mappingPath = "index/index-mapping.json")
public record Customer(@Id String id,
                       String name,
                       Integer age) {
}
