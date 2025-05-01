package com.shieldteq.elastic.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.shieldteq.elastic.dto.SearchRequest;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record QueryRule(Predicate<SearchRequest> predicate,
                        Function<SearchRequest, Query> queryFunction) {

    public static QueryRule of(Predicate<SearchRequest> predicate, Function<SearchRequest, Query> queryFunction) {
        return new QueryRule(predicate, queryFunction);
    }

    public Optional<Query> build(SearchRequest request) {
        return Optional.ofNullable(request)
                .filter(predicate())
                .map(queryFunction());
    }
}
