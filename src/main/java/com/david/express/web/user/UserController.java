package com.david.express.web.user;

import com.david.express.entity.User;
import com.david.express.service.UserService;
import com.david.express.web.user.dto.UserDTO;
import com.david.express.web.user.dto.UserResponseDTO;
import com.david.express.web.user.dto.UserUpdateDTO;
import com.david.express.web.user.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getAllUsers() {
        List<UserDTO> users = userService
                .findAllUsers()
                .stream()
                .map(UserDTOMapper::toUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new UserResponseDTO(users, users.size()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(UserDTOMapper.toUserDTO(user));
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDto) {
        User user = userService.updateUser(id, userUpdateDto);
        return ResponseEntity.ok(UserDTOMapper.toUserDTO(user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}