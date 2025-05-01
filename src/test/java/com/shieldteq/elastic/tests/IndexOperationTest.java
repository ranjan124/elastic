package com.shieldteq.elastic.tests;

import com.shieldteq.elastic.AbstractITTests;
import com.shieldteq.elastic.model.Customer;
import com.shieldteq.elastic.model.Movie;
import com.shieldteq.elastic.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

@Slf4j
public class IndexOperationTest extends AbstractITTests {
    @Autowired
    private ElasticsearchOperations operations;

    @Test
    public void createIndexTests() {
        IndexOperations operation = operations.indexOps(IndexCoordinates.of("products"));
        Assertions.assertTrue(operation.create());
        verify(operation, 1, 1);
    }

    @Test
    public void createIndexWithSettingsTests() {
        IndexOperations operation = operations.indexOps(Review.class);
        Assertions.assertTrue(operation.create());
        verify(operation, 2, 2);
    }


    @Test
    public void createIndexWithSettingsAndMappingTests() {
        IndexOperations operation = operations.indexOps(Customer.class);
        Assertions.assertTrue(operation.createWithMapping());
        verify(operation, 3, 0);
    }


    @Test
    public void createIndexWithFieldMappingTests() {
        IndexOperations operation = operations.indexOps(Movie.class);
        Assertions.assertTrue(operation.createWithMapping());
        verify(operation, 1, 1);
    }


    private void verify(IndexOperations op, int expectedShards, int expectedReplicas) {
        Settings settings = op.getSettings();
        log.info("Settings : {}", settings);
        log.info("Mappings : {}", op.getMapping());
        Assertions.assertEquals(String.valueOf(expectedShards), settings.get("index.number_of_shards"));
        Assertions.assertEquals(String.valueOf(expectedReplicas), settings.get("index.number_of_replicas"));

        // delete the index
        Assertions.assertTrue(op.delete());

    }
}
