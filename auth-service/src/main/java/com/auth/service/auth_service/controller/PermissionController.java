package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.PermissionRequestDTO;
import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.service.IPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
//@PreAuthorize("denyAll()")
public class PermissionController {

    private final IPermissionService  permissionService;

    @GetMapping
    //@PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<List<PermissionResponseDTO>> getAllPermission() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponseDTO> getPermissionFindById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PermissionResponseDTO> createPermission(@RequestBody @Valid PermissionRequestDTO permissionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permissionService.save(permissionRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePermissionByIs(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDTO> updatePermissionById(@PathVariable Long id, @RequestBody @Valid PermissionRequestDTO permissionRequestDTO) {
        return ResponseEntity.ok(permissionService.updateById(id,permissionRequestDTO));
    }

}
