package com.shieldteq.elastic.tests;

import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shieldteq.elastic.AbstractITTests;
import com.shieldteq.elastic.model.Garment;
import com.shieldteq.elastic.repository.GarmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class NativeAndCriteriaQueryTest extends AbstractITTests {
    @Autowired
    private GarmentRepository repository;
    @Autowired
    private ElasticsearchOperations operations;

    @BeforeAll
    public void setup() {
        List<Garment> garments = readResource("data/garments.json", new TypeReference<>() {
        });
        repository.saveAll(garments);
        Assertions.assertEquals(20, repository.count());
    }

    @Test
    public void criteriaQueryTest() {
        Criteria criteria = Criteria.where("name").is("shirt");
        validate(criteria, 1);

        Criteria priceAbove = Criteria.where("price").greaterThan(100);
        validate(priceAbove, 5);

        validate(criteria.or(priceAbove), 6);

        Criteria zara = Criteria.where("brand").is("Zara");

        validate(priceAbove.and(zara.not()), 3);

        Criteria fuzzy = Criteria.where("name").fuzzy("short");

        validate(fuzzy, 1);
    }

    /*
        {
          "query": {
            "bool": {
              "filter": [
                {
                  "term": {
                    "occasion": "Casual"
                  }
                },
                {
                    "range": {
                      "price": {
                        "lte": 50
                      }
                    }
                }
              ],
              "should": [
                {
                  "term": {
                    "color": "Brown"
                  }
                }
              ]
            }
          }
        }
    */
    @Test
    public void boolQueryTest() {
        Query casual = Query.of(b -> b.term(
                TermQuery.of(tb -> tb.field("occasion").value("Casual"))
        ));
        Query brown = Query.of(b -> b.term(
                TermQuery.of(tb -> tb.field("color").value("Brown"))
        ));
        Query range = Query.of(b -> b.range(
                RangeQuery.of(rb -> rb.number(NumberRangeQuery.of(nrb -> nrb.field("price").lte(50d)))

                )
        ));
        Query query = Query.of(b -> b.bool(
                BoolQuery.of(bb -> bb.filter(casual, range).should(brown))));

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query).build();

        SearchHits<Garment> searchHits = operations.search(nativeQuery, Garment.class);
        searchHits.forEach(print());
        Assertions.assertEquals(4, searchHits.getTotalHits());
    }

    /*
    {
      "size": 0,
      "aggs": {
        "price-stats": {
          "stats": {
            "field": "price"
          }
        },
        "group-by-brand": {
          "terms": {
            "field": "brand"
          }
        },
        "group-by-color": {
          "terms": {
            "field": "color"
          }
        },
        "price-range": {
          "range": {
            "field": "price",
            "ranges": [
              {
                "to": 50
              },
              {
                "from": 50,
                "to": 100
              },
              {
                "from": 100,
                "to": 150
              },
              {
                "from": 150
              }
            ]
          }
        }
      }
    }
     */
    @Test
    public void aggregationTest() {
        Aggregation price = Aggregation.of(b -> b.stats(
                StatsAggregation.of(sa -> sa.field("price"))));

        Aggregation brand = Aggregation.of(b -> b.terms(
                TermsAggregation.of(tb -> tb.field("brand"))
        ));
        Aggregation color = Aggregation.of(b -> b.terms(
                TermsAggregation.of(tb -> tb.field("color"))
        ));

        List<AggregationRange> ranges = List.of(
                AggregationRange.of(b -> b.to(50d)),
                AggregationRange.of(b -> b.from(50d).to(100d)),
                AggregationRange.of(b -> b.from(100d).to(150d)),
                AggregationRange.of(b -> b.from(150d))
        );
        Aggregation priceRange = Aggregation.of(b -> b.range(
                RangeAggregation.of(rb -> rb.field("price").ranges(ranges))
        ));

        NativeQuery query = NativeQuery.builder()
                .withMaxResults(0)
                .withAggregation("price-stats", price)
                .withAggregation("group-by-brand", brand)
                .withAggregation("group-by-color", color)
                .withAggregation("price-range", priceRange)

                .build();

        SearchHits<Garment> searchHits = operations.search(query, Garment.class);
        List<ElasticsearchAggregation> aggregations = (List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations();
        Map<String, Aggregate> map = aggregations.stream()
                .map(ElasticsearchAggregation::aggregation)
                .collect(Collectors.toMap(u -> u.getName(), v -> v.getAggregate()));
        print().accept(map);

        Assertions.assertEquals(4, map.size());

    }

    /*
    {
      "suggest": {
        "product-suggest": {
          "prefix": "ca",
          "completion": {
              "field": "name.completion"
          }
        }
      },
      "_source": false
    }
     */
    @Test
    public void suggestionTest() {
        FieldSuggester fieldSuggester = FieldSuggester.of(b -> b.prefix("ca").completion(
                CompletionSuggester.of(cab -> cab.field("name.completion").skipDuplicates(true).size(10))
        ));
        Suggester suggester = Suggester.of(b -> b.suggesters("product-suggest", fieldSuggester));

        NativeQuery nativeQuery = NativeQuery.builder()
                .withSuggester(suggester)
                .withMaxResults(0)
                .withSourceFilter(FetchSourceFilter.of(fb -> fb.withExcludes("*")))
                .build();

        SearchHits<Garment> searchHits = operations.search(nativeQuery, Garment.class);
        Assertions.assertNotNull(searchHits.getSuggest());
        List<String> stringStream = searchHits.getSuggest().getSuggestion("product-suggest")
                .getEntries().getFirst()
                .getOptions()
                .stream()
                .map(o -> o.getText())
                .toList();

        stringStream.forEach(print());
    }

    private void validate(Criteria criteria, int totalHits) {
        CriteriaQuery query = CriteriaQuery.builder(criteria).build();
        SearchHits<Garment> garments = operations.search(query, Garment.class);
        garments.forEach(print());
        Assertions.assertEquals(totalHits, garments.getTotalHits());
    }
}
