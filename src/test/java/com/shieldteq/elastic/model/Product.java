package com.shieldteq.elastic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Document(indexName = "product")
@Mapping(mappingPath = "index/product-mapping.json")
public record Product(@Id Integer id,
                      String name,
                      String brand,
                      String category,
                      Integer price,
                      Integer quantity) {
}
