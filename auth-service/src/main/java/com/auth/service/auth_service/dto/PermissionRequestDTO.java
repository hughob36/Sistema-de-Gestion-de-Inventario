package com.auth.service.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDTO {

    @NotBlank(message = "Permission cannot be empty.")
    @Size(min = 3, max = 40, message = "Permission must be between 3 and 50 characters.")
    private String permissionName;
}
