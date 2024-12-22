package com.david.express.web;

import com.david.express.service.TrendingService;
import com.david.express.model.dto.TrendingResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/trending")
public class TrendingController {

    @Autowired
    private TrendingService trendingService;

    @GetMapping("")
    public ResponseEntity<TrendingResponseDto> getTrending() {
        return ResponseEntity.ok(new TrendingResponseDto(trendingService.getTrendingWords()));
    }
}