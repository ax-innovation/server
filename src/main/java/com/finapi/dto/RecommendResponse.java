package com.finapi.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class RecommendResponse {
    private RecommendRequest    request;
    private List<RecommendItem> results;
}
