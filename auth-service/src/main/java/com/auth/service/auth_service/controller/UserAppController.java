package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.UserAppRequestDTO;
import com.auth.service.auth_service.dto.UserAppResponseDTO;
import com.auth.service.auth_service.model.UserApp;
import com.auth.service.auth_service.service.IUserAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserAppController {

    private final IUserAppService userAppService;

    @GetMapping
    public ResponseEntity<List<UserAppResponseDTO>> getUsers() {
        return ResponseEntity.ok(userAppService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAppResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok( userAppService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserAppResponseDTO> createUser(@RequestBody @Valid UserAppRequestDTO userAppRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userAppService.save(userAppRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id) {
        userAppService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAppResponseDTO> updateUserById(@PathVariable Long id, @RequestBody @Valid UserAppRequestDTO userAppRequestDTO) {
        return ResponseEntity.ok(userAppService.save(userAppRequestDTO));
    }
}
