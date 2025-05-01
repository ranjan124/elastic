package com.shieldteq.elastic.dto;

import java.util.List;

public record Facet(String name,
                    List<FacetItem> items) {
}
