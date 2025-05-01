package com.shieldteq.elastic.dto;

import com.shieldteq.elastic.exception.BadRequestException;
import org.springframework.util.StringUtils;

import java.util.Objects;

public record SuggestionRequest(String prefix,
                                Integer limit) {
    public SuggestionRequest {
        if (!StringUtils.hasText(prefix)) throw new BadRequestException("query can not be empty");
        limit = Objects.requireNonNullElse(limit, 0);
    }
}
