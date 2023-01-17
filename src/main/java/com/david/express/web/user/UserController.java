package com.david.express.web.user;

import com.david.express.model.Role;
import com.david.express.model.User;
import com.david.express.repository.UserRepository;
import com.david.express.service.RoleService;
import com.david.express.service.UserService;
import com.david.express.validation.dto.SuccessResponseDTO;
import com.david.express.web.user.dto.UserDTO;
import com.david.express.web.user.dto.UserUpdateDTO;
import com.david.express.web.user.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> updateUserById(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO user) {
        User userToUpdate = userService.findUserById(id);
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        userToUpdate.setRoles(null);
        Set<Role> roles = roleService.rolesAssignmentByAdmin(user.getRoles());
        userToUpdate.setRoles(roles);
        userRepository.save(userToUpdate);
        return ResponseEntity.ok(new SuccessResponseDTO("User has been updated successfully !"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponseDTO> deleteUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        userService.deleteUserById(user.getId());
        return ResponseEntity.ok(new SuccessResponseDTO("User has been deleted successfully !"));
    }
}
