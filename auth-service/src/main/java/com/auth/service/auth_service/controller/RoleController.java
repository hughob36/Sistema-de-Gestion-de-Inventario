package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.RoleRequestDTO;
import com.auth.service.auth_service.dto.RoleResponseDTO;
import com.auth.service.auth_service.service.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> getAllRole() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody @Valid RoleRequestDTO roleRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.save(roleRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRoleById(@PathVariable Long id) {
       roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> updateRoleById(@PathVariable Long id, @RequestBody @Valid RoleRequestDTO roleRequestDTO) {
        return ResponseEntity.ok(roleService.updateById(id,roleRequestDTO));
    }

}
