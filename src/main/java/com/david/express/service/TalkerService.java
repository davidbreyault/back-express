package com.david.express.service;

import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.model.dto.TalkerDto;

public interface TalkerService {
    PaginatedResponseDto<TalkerDto> getTalkers(int page, int size, String[] sort);
}
