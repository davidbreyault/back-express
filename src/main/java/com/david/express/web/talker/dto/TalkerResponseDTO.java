package com.david.express.web.talker.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class TalkerResponseDTO {

    private HashMap<String, ActivityDTO> talkers;
    private int totalUsers;
    private int totalTalkers;
    private Long ts;

    public TalkerResponseDTO(HashMap<String, ActivityDTO> talkers, int totalUsers) {
        this.talkers = talkers;
        this.totalUsers = totalUsers;
        this.totalTalkers = talkers.size();
        this.ts = System.currentTimeMillis();
    }
}
