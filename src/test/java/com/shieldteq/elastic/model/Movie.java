package com.shieldteq.elastic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "movie")
public record Movie(@Id String id,
                    @Field(type = FieldType.Text)
                    String title,
                    @Field(name = "genre", type = FieldType.Keyword)
                    String category,
                    @Field(type = FieldType.Integer)
                    Integer rating) {
}
