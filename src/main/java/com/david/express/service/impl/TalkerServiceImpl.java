package com.david.express.service.impl;

import com.david.express.common.Utils;
import com.david.express.entity.User;
import com.david.express.model.dto.PaginatedResponseDto;
import com.david.express.model.dto.TalkerDto;
import com.david.express.model.mapper.TalkerMapper;
import com.david.express.service.TalkerService;
import com.david.express.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TalkerServiceImpl implements TalkerService {

    @Autowired
    private UserService userService;

    @Override
    public PaginatedResponseDto<TalkerDto> getTalkers(int page, int size, String[] sort) {
        // Créer le Pageable avec le tri
        Pageable pageable = Utils.createPaging(page, size, sort);

        // Récupération des utilisateurs
        Page<User> pageUsers = userService.findAllUsers(pageable);

        // Mapping
        List<TalkerDto> talkers = pageUsers.stream().map(TalkerMapper::toTalkerDto).collect(Collectors.toList());

        // Création de la réponse avec les informations de pagination
        PaginatedResponseDto<TalkerDto> response = new PaginatedResponseDto<TalkerDto>();
        response.setKey("talkers");
        response.setData(talkers);
        response.setCurrentPage(Optional.of(pageUsers).map(Page::getNumber).orElse(0));
        response.setTotalItems(Optional.of(pageUsers).map(Page::getTotalElements).orElse(0L));
        response.setTotalPages(Optional.of(pageUsers).map(Page::getTotalPages).orElse(0));

        return response;
    }
}
