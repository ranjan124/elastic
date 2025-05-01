package com.shieldteq.elastic.dto;

public record Pagination(int page,
                         int size,
                         long totalElements,
                         int totalPages) {
}
