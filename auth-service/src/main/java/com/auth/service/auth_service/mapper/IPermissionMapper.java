package com.auth.service.auth_service.mapper;

import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.model.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IPermissionMapper {

    public List<PermissionResponseDTO> toPermissionResponseDTO(List<Permission> permissionList);


}
