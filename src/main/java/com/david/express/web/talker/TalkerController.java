package com.david.express.web.talker;

import com.david.express.entity.User;
import com.david.express.service.UserService;
import com.david.express.web.talker.dto.ActivityDTO;
import com.david.express.web.talker.dto.TalkerResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/talkers")
public class TalkerController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<TalkerResponseDTO> getTalkers() {
        return ResponseEntity.ok(
                new TalkerResponseDTO(buildTalkersHashMap(), userService.findAllUsers().size())
        );
    };

    private HashMap<String, ActivityDTO> buildTalkersHashMap() {
        List<User> users = userService.findAllUsers();
        HashMap<String, ActivityDTO> talkers = new HashMap<>();
        users.forEach(user -> {
            if (user.getNotes().size() > 0 || user.getComments().size() > 0) {
                talkers.put(user.getUsername(), new ActivityDTO(
                        user.getNotes().size(),
                        user.getComments().size())
                );
            }
        });
        return talkers;
    }
}
