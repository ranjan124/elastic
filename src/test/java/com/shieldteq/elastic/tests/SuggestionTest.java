package com.shieldteq.elastic.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shieldteq.elastic.AbstractITTests;
import com.shieldteq.elastic.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.http.ProblemDetail;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class SuggestionTest extends AbstractITTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ElasticsearchOperations operations;
    public static final String API_PATH = "/api/suggestions?%s";


    @BeforeAll
    public void setup(){
        var indexMapping = this.readResource("index/suggestion-index-mapping.json", new TypeReference<Map<String, Object>>() {
        });
        var suggestionData = this.readResource("data/suggestion-data.json", new TypeReference<List<Object>>() {
        });
        var indexOperations = this.operations.indexOps(Constant.Index.SUGGESTION);
        indexOperations.create(Collections.emptyMap(), Document.from(indexMapping));

        operations.withRefreshPolicy(RefreshPolicy.IMMEDIATE).save(suggestionData, Constant.Index.SUGGESTION);
        var searchHits = operations.search(operations.matchAllQuery(), Object.class, Constant.Index.SUGGESTION);
        Assertions.assertEquals(4, searchHits.getTotalHits());
    }

    @ParameterizedTest
    @MethodSource("successTestData")
    public void suggestionsSuccessTest(String parameters, List<String> expectedResults){
        var path = API_PATH.formatted(parameters);
        var responseEntity = this.restTemplate.exchange(
                RequestEntity.get(URI.create(path)).build(),
                new ParameterizedTypeReference<List<String>>() {
                }
        );
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        log.info("response: {}", responseEntity.getBody());
        Assertions.assertEquals(expectedResults, responseEntity.getBody());
    }

    @ParameterizedTest
    @MethodSource("failureTestData")
    public void suggestionsFailureTest(String parameters){
        var path = API_PATH.formatted(parameters);
        var responseEntity = this.restTemplate.getForEntity(URI.create(path), ProblemDetail.class);
        Assertions.assertTrue(responseEntity.getStatusCode().is4xxClientError());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals("prefix can not be empty", responseEntity.getBody().getDetail());
    }

    private Stream<Arguments> successTestData() {
        return Stream.of(
                Arguments.of("prefix=w", List.of("walmart"))
//                Arguments.of("prefix=c", List.of("cafe", "coffee")),
//                Arguments.of("prefix=c&limit=1", List.of("cafe")),
//                Arguments.of("prefix=co", List.of("coffee")),
//                Arguments.of("prefix=cofe", List.of("coffee")), // fuzzy - but not cafe because of prefix 2
//                Arguments.of("prefix=cffee", List.of()), // fuzzy prefix length 2
//                Arguments.of("prefix=12", List.of()),
//                Arguments.of("prefix=x", List.of())
        );
    }

    private static Stream<Arguments> failureTestData() {
        return Stream.of(
                Arguments.of("prefix="),
                Arguments.of("")
        );
    }

}
