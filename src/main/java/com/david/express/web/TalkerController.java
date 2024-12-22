package com.david.express.web;

import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.model.dto.TalkerDto;
import com.david.express.service.TalkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/talkers")
public class TalkerController {

    @Autowired
    private TalkerService talkerService;

    @GetMapping("")
    public ResponseEntity<PaginatedResponseDto<TalkerDto>> getTalkers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size,
            @RequestParam(defaultValue = "id, desc") String[] sort
    ) {
        PaginatedResponseDto<TalkerDto> response = talkerService.getTalkers(page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    };
}
