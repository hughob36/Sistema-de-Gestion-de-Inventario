package com.auth.service.auth_service.mapper;


import com.auth.service.auth_service.dto.UserAppRequestDTO;
import com.auth.service.auth_service.dto.UserAppResponseDTO;
import com.auth.service.auth_service.model.UserApp;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    public List<UserAppResponseDTO> toUserAppResponseDTOList(List<UserApp> UserAppList);
    public UserAppResponseDTO toUserAppResponseDTO(UserApp userApp);
    public UserApp toUserApp(UserAppRequestDTO userAppRequestDTO);

    public void updateUserFromDTO(UserAppRequestDTO dto, @MappingTarget UserApp userApp);
}
