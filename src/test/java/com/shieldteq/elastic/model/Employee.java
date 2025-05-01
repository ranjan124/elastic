package com.shieldteq.elastic.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Document(indexName = "employee")
@Mapping(mappingPath = "index/index-mapping-with-id.json")
@Builder
public record Employee(@Id String id,
                       String name,
                       Integer age) {
}
