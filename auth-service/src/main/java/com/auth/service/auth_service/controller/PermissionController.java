package com.auth.service.auth_service.controller;

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
    public ResponseEntity<Permission> getPermissionFindById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.findBYId(id));
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody @Valid Permission permission) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permissionService.save(permission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePermissionByIs(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermissionById(@PathVariable Long id, @RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.updateById(id,permission));
    }

}
