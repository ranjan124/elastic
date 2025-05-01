package com.shieldteq.elastic.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import com.shieldteq.elastic.constant.Constant;
import com.shieldteq.elastic.dto.*;
import com.shieldteq.elastic.util.NativeQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.Aggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchOperations esOperation;

    public SearchResponse search(SearchRequest request) {
        log.info("Search Request {}", request);
        NativeQuery query = NativeQueryBuilder.toSearchQuery(request);
        log.info("bool query : {}", query.getQuery());
        SearchHits<Business> searchHits = esOperation.search(query, Business.class, Constant.Index.BUSINESS);
        return buildResponse(request, searchHits);

    }

    private SearchResponse buildResponse(SearchRequest request, SearchHits<Business> searchHits) {
        List<Business> results = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();

        SearchPage<Business> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(request.page(), request.size()));
        Pagination pagination = new Pagination(
                searchPage.getNumber(),
                searchPage.getNumberOfElements(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );
        List<Facet> facets = buildFacets((List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations());
        return new SearchResponse(
                results,
                facets,
                pagination,
                searchHits.getExecutionDuration().toMillis()
        );
    }

    private List<Facet> buildFacets(List<ElasticsearchAggregation> aggregations) {
        Map<String, Aggregate> map = aggregations.stream()
                .map(ElasticsearchAggregation::aggregation)
                .collect(Collectors.toMap(
                        Aggregation::getName,
                        Aggregation::getAggregate
                ));

        return List.of(
                buildFacet(Constant.Business.OFFERINGS_AGGREGATE_NAME, map.get(Constant.Business.OFFERINGS_AGGREGATE_NAME).sterms())
        );
    }

    private Facet buildFacet(String name, StringTermsAggregate stringTermsAggregate) {
        List<FacetItem> list = stringTermsAggregate.buckets().array().stream()
                .map(b -> new FacetItem(b.key().stringValue(), b.docCount()))
                .toList();

        return new Facet(name, list);

    }
}
