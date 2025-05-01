package com.shieldteq.elastic.dto;

import com.shieldteq.elastic.exception.BadRequestException;
import org.springframework.util.StringUtils;

import java.util.Objects;

public record SearchRequest(String query,
                            String distance,
                            Double latitude,
                            Double longitude,
                            Double rating,
                            String state,
                            String offering,
                            Integer page,
                            Integer size) {
    public SearchRequest {
        if (!StringUtils.hasText(query)) throw new BadRequestException("query can not be empty");
        page = Objects.requireNonNullElse(page, 0);
        size = Objects.requireNonNullElse(size, 10);
    }
}
