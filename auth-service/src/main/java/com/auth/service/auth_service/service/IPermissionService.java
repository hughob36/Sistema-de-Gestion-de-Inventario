package com.auth.service.auth_service.service;


import com.auth.service.auth_service.dto.PermissionRequestDTO;
import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.model.Permission;

import java.util.List;

public interface IPermissionService {

    public List<PermissionResponseDTO> findAll();
    public PermissionResponseDTO findById(Long id);
    public PermissionResponseDTO save(PermissionRequestDTO permissionRequestDTO);
    public void deleteById(Long id);
    public PermissionResponseDTO updateById(Long id, PermissionRequestDTO permissionRequestDTO);
}

