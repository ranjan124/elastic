package com.shieldteq.elastic.service;

import com.shieldteq.elastic.constant.Constant;
import com.shieldteq.elastic.dto.SuggestionRequest;
import com.shieldteq.elastic.util.NativeQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final ElasticsearchOperations esOperation;

    public List<String> fetchSuggestions(SuggestionRequest request) {
        log.info("suggestion request: {}", request);
        var query = NativeQueryBuilder.toSuggestQuery(request);
        var searchHits = esOperation.search(query, Object.class, Constant.Index.SUGGESTION);
        return Optional.ofNullable(searchHits.getSuggest())
                .map(s -> s.getSuggestion(Constant.Suggestion.SUGGEST_NAME))
                .stream()
                .map(Suggest.Suggestion::getEntries)
                .flatMap(Collection::stream)
                .map(Suggest.Suggestion.Entry::getOptions)
                .flatMap(Collection::stream)
                .map(Suggest.Suggestion.Entry.Option::getText)
                .toList();
    }
}
