package com.auth.service.auth_service.dto;

import com.auth.service.auth_service.model.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDTO {

    @NotBlank(message = "Role cannot be empty.")
    @Size(min = 2, max = 40, message = "Role must be between 2 and 40 characters.")
    private String role;

    private Set<Permission> permissionSet = new HashSet<>();
}
