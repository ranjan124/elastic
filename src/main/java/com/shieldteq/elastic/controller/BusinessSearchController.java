package com.shieldteq.elastic.controller;

import com.shieldteq.elastic.dto.SearchRequest;
import com.shieldteq.elastic.dto.SearchResponse;
import com.shieldteq.elastic.dto.SuggestionRequest;
import com.shieldteq.elastic.service.SearchService;
import com.shieldteq.elastic.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BusinessSearchController {
    private final SuggestionService suggestionService;
    private final SearchService searchService;

    @GetMapping("/suggestions")
    public List<String> suggest(SuggestionRequest parameters) {
        return suggestionService.fetchSuggestions(parameters);
    }

    @GetMapping("/search")
    public SearchResponse search(SearchRequest parameters) {
        return searchService.search(parameters);
    }
}
