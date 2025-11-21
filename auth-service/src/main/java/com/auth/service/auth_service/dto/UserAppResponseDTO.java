package com.auth.service.auth_service.dto;

import com.auth.service.auth_service.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAppResponseDTO {

    private Long id;
    private String name;
    private String lastname;
    private String dni;
    private String username;
    private String password;
    private boolean enable;
    private boolean accountNotExpired;
    private boolean accountNotLocked;
    private boolean credentialNotExpired;
    private Set<Role> roleSet = new HashSet<>();
}
