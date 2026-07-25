package com.auth.service.auth_service.service;

import com.auth.service.auth_service.dto.PermissionRequestDTO;
import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.mapper.IPermissionMapper;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.repository.IPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService{


    private final IPermissionRepository permissionRepository;
    private final IPermissionMapper permissionMapper;

    @Override
    public List<PermissionResponseDTO> findAll() {
        return permissionMapper.toPermissionResponseDTOList(permissionRepository.findAll());
    }

    @Override
    public PermissionResponseDTO findById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        return permissionMapper.toPermissionResponseDTO(permission);
    }

    @Override
    public PermissionResponseDTO save(PermissionRequestDTO permissionRequestDTO) {
        Permission permission = permissionMapper.toPermission(permissionRequestDTO);
        return permissionMapper.toPermissionResponseDTO(permissionRepository.save(permission));
    }

    @Override
    public void deleteById(Long id) {
        if(!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not exists.");
        }
        permissionRepository.deleteById(id);
    }

    @Override
    public PermissionResponseDTO updateById(Long id, PermissionRequestDTO permissionRequestDTO) {
        Permission permissionFound = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
        permissionMapper.updatePermissionFromDto(permissionRequestDTO,permissionFound);
        return permissionMapper.toPermissionResponseDTO(permissionRepository.save(permissionFound));
    }
}
