package com.volttrack.volttrack.controller;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;
import com.volttrack.volttrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(userService.createUser(requestDto));
    }

   @GetMapping
public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getAllUsers(pageable));
}


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
