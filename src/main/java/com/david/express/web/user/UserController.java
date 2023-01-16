package com.david.express.web.user;

import com.david.express.model.User;
import com.david.express.service.UserService;
import com.david.express.validation.dto.SuccessResponseDTO;
import com.david.express.web.user.dto.UserDTO;
import com.david.express.web.user.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService
                .findAllUsers()
                .stream()
                .map(UserDTOMapper::toUserDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        System.out.println(user.getUsername());
        return ResponseEntity.ok(UserDTOMapper.toUserDTO(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> deleteUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        userService.deleteUserById(user.getId());
        return ResponseEntity.ok(new SuccessResponseDTO("User has been deleted successfully !"));
    }
}
