package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.model.UserApp;
import com.auth.service.auth_service.service.IUserAppService;
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
    public ResponseEntity<List<UserApp>> getUsers() {
        return ResponseEntity.ok(userAppService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserApp> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok( userAppService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserApp> createUser(@RequestBody UserApp userApp) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userAppService.save(userApp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id) {
        userAppService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserApp> updateUserById(@PathVariable Long id, @RequestBody UserApp userApp) {
        return ResponseEntity.ok(userAppService.save(userApp));
    }
}
