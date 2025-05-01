package com.shieldteq.elastic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Document(indexName = "article")
@Mapping(mappingPath = "index/articles-mapping.json")
public record Article(@Id String id,
                      String title,
                      String body) {
}
