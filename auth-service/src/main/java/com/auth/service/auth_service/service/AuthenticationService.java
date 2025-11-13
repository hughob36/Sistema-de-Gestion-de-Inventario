package com.auth.service.auth_service.service;

import com.auth.service.auth_service.dto.AuthRequestDTO;
import com.auth.service.auth_service.dto.AuthResponseDTO;
import com.auth.service.auth_service.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService{

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO loginUser(AuthRequestDTO authRequestDTO) {

        String username = authRequestDTO.username();
        String password = authRequestDTO.password();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.createToken(authentication);
        return new AuthResponseDTO(username,"Successful",token,true);
    }
}
