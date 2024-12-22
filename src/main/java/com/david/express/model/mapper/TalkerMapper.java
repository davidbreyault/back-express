package com.david.express.model.mapper;

import com.david.express.entity.User;
import com.david.express.model.dto.TalkerDto;

public class TalkerMapper {

    public static TalkerDto toTalkerDto(User user) {
        return new TalkerDto(
            user.getUsername(),
            user.getNotes().size(),
            user.getComments().size()
        );
    }
}
