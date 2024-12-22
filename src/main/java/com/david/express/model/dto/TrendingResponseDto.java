package com.david.express.model.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class TrendingResponseDto {

    private HashMap<String, Integer> trending;
    private int totalWords;
    private Long ts;

    public TrendingResponseDto(HashMap<String, Integer> trending) {
        this.trending = trending;
        this.totalWords = trending.size();
        this.ts = System.currentTimeMillis();
    }
}
