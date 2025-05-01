package com.shieldteq.elastic.util;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.shieldteq.elastic.constant.Constant;

import java.util.List;
import java.util.function.UnaryOperator;

public class ElasticSearchUtil {
    private ElasticSearchUtil() {
    }

    public static Suggester buildCompletionSuggester(String suggestName, String field, String prefix, int limit) {
        SuggestFuzziness suggestFuzzy = SuggestFuzziness.of(b -> b.fuzziness(Constant.Fuzzy.LEVEL).prefixLength(Constant.Fuzzy.PREFIX_LENGTH));
        CompletionSuggester completionSuggester = CompletionSuggester.of(b -> b.field(field).size(limit).fuzzy(suggestFuzzy).skipDuplicates(true));
        FieldSuggester fieldSuggester = FieldSuggester.of(b -> b.prefix(prefix).completion(completionSuggester));
        return Suggester.of(b -> b.suggesters(suggestName, fieldSuggester));
    }

    public static Query buildTermQuery(String field, String value, float boost) {
        TermQuery termQuery = TermQuery.of(b -> b.field(field).value(value).boost(boost).caseInsensitive(true));
        return Query.of(b -> b.term(termQuery));
    }

    public static Query buildRangeQuery(String field, UnaryOperator<NumberRangeQuery.Builder> rqBuilder) {
        NumberRangeQuery numberRangeQuery = NumberRangeQuery.of(b -> rqBuilder.apply(b.field(field)));
        RangeQuery rangeQuery = RangeQuery.of(b -> b.number(numberRangeQuery));
        return Query.of(b -> b.range(rangeQuery));
    }

    public static Query buildGeoDistanceQuery(String field, String distance, Double latitude, Double longitude) {
        LatLonGeoLocation location = LatLonGeoLocation.of(b -> b.lat(latitude).lon(longitude));
        GeoLocation geoLocation = GeoLocation.of(b -> b.latlon(location));
        GeoDistanceQuery geoDistanceQuery = GeoDistanceQuery.of(b -> b.field(field).distance(distance).location(geoLocation));
        return Query.of(b -> b.geoDistance(geoDistanceQuery));
    }

    public static Query buildMultiMatchQuery(List<String> fields, String searchTerm) {
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(b -> b.query(searchTerm)
                .fields(fields)
                .fuzziness(Constant.Fuzzy.LEVEL)
                .prefixLength(Constant.Fuzzy.PREFIX_LENGTH)
                .type(TextQueryType.MostFields)
                .operator(Operator.And));
        return Query.of(b -> b.multiMatch(multiMatchQuery));
    }

    public static Aggregation buildTermsAggregation(String field) {
        TermsAggregation termsAggregation = TermsAggregation.of(b -> b.field(field).size(10));
        return Aggregation.of(b -> b.terms(termsAggregation));
    }
}
