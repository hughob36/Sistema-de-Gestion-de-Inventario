package com.auth.service.auth_service.service;

import com.auth.service.auth_service.model.UserApp;
import com.auth.service.auth_service.repository.IUserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserAppRepository userAppRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserApp userApp = userAppRepository.findEntityUserAppByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userApp.getRoleSet()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole()))));

        userApp.getRoleSet().stream()
                .flatMap(role -> role.getPermissionSet().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        return new User(
                userApp.getUsername(),
                userApp.getPassword(),
                userApp.isEnable(),
                userApp.isAccountNotExpired(),
                userApp.isCredentialNotExpired(),
                userApp.isAccountNotLocked(),
                authorityList);
    }
}
