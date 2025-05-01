package com.shieldteq.elastic.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shieldteq.elastic.AbstractITTests;
import com.shieldteq.elastic.model.Product;
import com.shieldteq.elastic.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

@Slf4j
public class QueryMethodTest extends AbstractITTests {
    @Autowired
    private ProductRepository repository;

    @BeforeAll
    public void setup() {
        List<Product> products = readResource("data/products.json", new TypeReference<>() {
        });
        repository.saveAll(products);
        Assertions.assertEquals(20, repository.count());
    }

    @Test
    public void findByCategoryTest() {
        SearchHits<Product> hits = repository.findByCategory("Furniture");
        hits.forEach(print());
        Assertions.assertEquals(4, hits.getTotalHits());
    }

    @Test
    public void findByCategoriesTest() {
        SearchHits<Product> hits = repository.findByCategoryIn(List.of("Furniture", "Beauty"));
        hits.forEach(print());
        Assertions.assertEquals(8, hits.getTotalHits());
    }
}
