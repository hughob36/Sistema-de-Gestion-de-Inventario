package com.auth.service.auth_service.service;

import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.model.Role;
import com.auth.service.auth_service.repository.IPermissionRepository;
import com.auth.service.auth_service.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
    }

    @Override
    public Role save(Role role) {
        Role roleValidate = this.validatePermissionsExists(role);
        return roleRepository.save(roleValidate);
    }

    @Override
    public void deleteById(Long id) {
        if(!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found.");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public Role updateById(Long id, Role role) {
        Role roleFound = this.findById(id);
        roleFound.setRole(role.getRole());
        Role roleValidate = this.validatePermissionsExists(roleFound);
        return roleRepository.save(roleValidate);
    }

    public Role validatePermissionsExists(Role role) {
        Set<Permission> permissionSet = new HashSet<>();
        for(Permission permission : role.getPermissionSet()) {
            permissionRepository.findById(role.getId()).ifPresent(permissionSet::add);
        }
        role.setPermissionSet(permissionSet);
        return role;
    }
}
