package com.david.express.web.trending;

import com.david.express.service.NoteService;
import com.david.express.web.trending.dto.TrendingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/trending")
public class TrendingController {

    @Autowired
    private NoteService noteService;

    @GetMapping("")
    public ResponseEntity<TrendingResponseDTO> getTrending() {
        return ResponseEntity.ok(new TrendingResponseDTO(noteService.getTrendingWords()));
    }
}
