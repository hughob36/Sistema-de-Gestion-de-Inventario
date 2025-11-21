package com.auth.service.auth_service.service;


import com.auth.service.auth_service.dto.UserAppRequestDTO;
import com.auth.service.auth_service.dto.UserAppResponseDTO;
import com.auth.service.auth_service.model.UserApp;

import java.util.List;

public interface IUserAppService {

    public List<UserAppResponseDTO> findAll();
    public UserAppResponseDTO findById(Long id);
    public UserAppResponseDTO save(UserAppRequestDTO userAppRequestDTO);
    public void deleteById(Long id);
    public UserAppResponseDTO updateById(Long id, UserAppRequestDTO userAppRequestDTO);
}
