package com.auth.service.auth_service.service;

import com.auth.service.auth_service.dto.UserAppRequestDTO;
import com.auth.service.auth_service.dto.UserAppResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.mapper.IUserMapper;
import com.auth.service.auth_service.model.Role;
import com.auth.service.auth_service.model.UserApp;
import com.auth.service.auth_service.repository.IRoleRepository;
import com.auth.service.auth_service.repository.IUserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserAppService {

    private final IUserAppRepository userAppRepository;
    private final IRoleRepository roleRepository;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserAppResponseDTO> findAll() {
        return userMapper.toUserAppResponseDTOList(userAppRepository.findAll());
    }

    @Override
    public UserAppResponseDTO findById(Long id) {
        UserApp userAppFound = userAppRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return userMapper.toUserAppResponseDTO(userAppFound);
    }

    @Override
    public UserAppResponseDTO save(UserAppRequestDTO userAppRequestDTO) {
        UserApp userApp = userMapper.toUserApp(userAppRequestDTO);
        UserApp userAppValidate = this.validateRoleExists(userApp);
        userAppValidate.setPassword(passwordEncoder.encode(userAppValidate.getPassword()));
        return userMapper.toUserAppResponseDTO(userAppRepository.save(userAppValidate));
    }

    @Override
    public void deleteById(Long id) {
        if(!userAppRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not exists.");
        }
        userAppRepository.deleteById(id);
    }

    @Override
    public UserAppResponseDTO updateById(Long id, UserAppRequestDTO userAppRequestDTO) {
        UserApp userAppFound = userAppRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        userMapper.updateUserFromDTO(userAppRequestDTO,userAppFound);

        UserApp userAppValidate = this.validateRoleExists(userAppFound);
        userAppValidate.setPassword(passwordEncoder.encode(userAppValidate.getPassword()));

        return userMapper.toUserAppResponseDTO(userAppRepository.save(userAppValidate));
    }

    public UserApp validateRoleExists(UserApp userApp) {
        Set<Role> roleSet = new HashSet<>();
        for(Role role : userApp.getRoleSet()) {
            roleRepository.findById(role.getId()).ifPresent(roleSet::add);
        }
        userApp.setRoleSet(roleSet);
        return userApp;
    }
}
