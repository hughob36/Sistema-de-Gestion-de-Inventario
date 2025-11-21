package com.auth.service.auth_service.dto;

import com.auth.service.auth_service.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAppRequestDTO {

    private String name;
    private String lastname;

    @NotBlank(message = "DNI cannot be empty.")
    @Size(min = 5, max = 20, message = "DNI must be between 5 and 20 characters.")
    @Pattern(regexp = "^[0-9]+$", message = "DNI must contain only numbers.")
    private String dni;

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters.")
    private String username;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters.")
    private String password;
    private boolean enable;
    private boolean accountNotExpired;
    private boolean accountNotLocked;
    private boolean credentialNotExpired;
    private Set<Role> roleSet = new HashSet<>();
}
