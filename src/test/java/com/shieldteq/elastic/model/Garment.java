package com.shieldteq.elastic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.List;

@Document(indexName = "garment")
@Mapping(mappingPath = "index/garment-mapping.json")
public record Garment(@Id String id,
                      String name,
                      List<String> size,
                      List<String> color,
                      String material,
                      String brand,
                      String occasion,
                      String neckStyle,
                      Integer price) {
}
