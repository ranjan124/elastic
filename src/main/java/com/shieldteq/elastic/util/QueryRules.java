package com.shieldteq.elastic.util;

import com.shieldteq.elastic.constant.Constant;
import org.springframework.data.util.Predicates;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.shieldteq.elastic.constant.Constant.Business.CATEGORY_RAW;
import static com.shieldteq.elastic.util.ElasticSearchUtil.*;

public class QueryRules {
    private QueryRules() {
    }

    public static final String BOOST_FIELD_FORMAT = "%s^%f";

    public static final QueryRule STATE_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.state()),
            srp -> buildTermQuery(Constant.Business.STATE, srp.state(), 1.0f)
    );
    public static final QueryRule OFFERING_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.offering()),
            srp -> buildTermQuery(Constant.Business.OFFERINGS_RAW, srp.offering(), 1.0f)
    );

    public static final QueryRule RATING_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.rating()),
            srp -> buildRangeQuery(Constant.Business.RATING, b -> b.lte(srp.rating())));

    public static final QueryRule DISTANCE_QUERY = QueryRule.of(
            srp -> Stream.of(srp.distance(), srp.longitude(), srp.latitude()).allMatch(Objects::nonNull),
            srp -> buildGeoDistanceQuery(Constant.Business.LOCATION, srp.distance(), srp.latitude(), srp.longitude()));

    public static final QueryRule CATEGORY_QUERY = QueryRule.of(
            Predicates.isTrue(),
            srp -> buildTermQuery(CATEGORY_RAW, srp.query(), 5.0f));

    private static final List<String> SEARCH_BOOST_FIELDS = List.of(
            boostField(Constant.Business.NAME, 2.0f),
            boostField(Constant.Business.CATEGORY, 1.5f),
            boostField(Constant.Business.OFFERINGS, 1.5f),
            boostField(Constant.Business.ADDRESS, 1.2f),
            Constant.Business.DESCRIPTION);

    public static final QueryRule SEARCH_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.query()),
            srp -> buildMultiMatchQuery(SEARCH_BOOST_FIELDS, srp.query()));

    private static String boostField(String field, float boost) {
        return BOOST_FIELD_FORMAT.formatted(field, boost);
    }

}
