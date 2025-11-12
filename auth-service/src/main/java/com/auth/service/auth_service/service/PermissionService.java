package com.auth.service.auth_service.service;

import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.repository.IPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService{

    @Autowired
    private final IPermissionRepository permissionRepository;

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission findBYId(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
    }

    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public void deleteById(Long id) {
        if(!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not exists.");
        }
        permissionRepository.deleteById(id);
    }

    @Override
    public Permission updateById(Long id, Permission permission) {
        Permission permissionFound = this.findBYId(id);
        permissionFound.setPermissionName(permission.getPermissionName());
        return permissionRepository.save(permissionFound);
    }
}
