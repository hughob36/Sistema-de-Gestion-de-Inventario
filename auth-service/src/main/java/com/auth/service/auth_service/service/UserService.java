package com.auth.service.auth_service.service;

import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.model.Role;
import com.auth.service.auth_service.model.UserApp;
import com.auth.service.auth_service.repository.IRoleRepository;
import com.auth.service.auth_service.repository.IUserAppRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserAppService {

    private final IUserAppRespository userAppRepository;
    private final IRoleRepository roleRepository;

    @Override
    public List<UserApp> findAll() {
        return userAppRepository.findAll();
    }

    @Override
    public UserApp findById(Long id) {
        return userAppRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Override
    public UserApp save(UserApp userApp) {
        UserApp userAppValidate = this.validateRoleExists(userApp);
        return userAppRepository.save(userAppValidate);
    }

    @Override
    public void deleteById(Long id) {
        if(!userAppRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not exists.");
        }
        userAppRepository.deleteById(id);
    }

    @Override
    public UserApp updateById(Long id, UserApp userApp) {

        UserApp userAppFound = this.findById(id);

        UserApp userAppValidate = this.validateRoleExists(userAppFound);

        return userAppRepository.save(userAppValidate);
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
