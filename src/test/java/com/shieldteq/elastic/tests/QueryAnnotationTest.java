package com.shieldteq.elastic.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shieldteq.elastic.AbstractITTests;
import com.shieldteq.elastic.model.Article;
import com.shieldteq.elastic.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

@Slf4j
public class QueryAnnotationTest extends AbstractITTests {
    @Autowired
    private ArticleRepository repository;

    @BeforeAll
    public void setup() {
        List<Article> products = readResource("data/articles.json", new TypeReference<>() {
        });
        repository.saveAll(products);
        Assertions.assertEquals(11, repository.count());
    }

    @Test
    public void findByCategoryTest() {
        SearchHits<Article> hits = repository.search("spring seasen");
        hits.forEach(print());
        Assertions.assertEquals(4, hits.getTotalHits());
    }

}
