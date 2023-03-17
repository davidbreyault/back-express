package com.david.express.web.trending;

import com.david.express.service.TrendingService;
import com.david.express.web.trending.dto.TrendingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/trending")
public class TrendingController {

    @Autowired
    private TrendingService trendingService;

    @GetMapping("")
    public ResponseEntity<TrendingResponseDTO> getTrending() {
        return ResponseEntity.ok(new TrendingResponseDTO(trendingService.getTrendingWords()));
    }
}