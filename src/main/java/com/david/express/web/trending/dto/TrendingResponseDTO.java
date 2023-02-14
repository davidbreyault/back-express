package com.david.express.web.trending.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class TrendingResponseDTO {

    private HashMap<String, Integer> trending;
    private int totalWords;
    private Long ts;

    public TrendingResponseDTO(HashMap<String, Integer> trending) {
        this.trending = trending;
        this.totalWords = trending.size();
        this.ts = System.currentTimeMillis();
    }
}
