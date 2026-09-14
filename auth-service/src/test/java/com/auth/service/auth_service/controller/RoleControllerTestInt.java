package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.RoleRequestDTO;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.model.Role;
import com.auth.service.auth_service.repository.IPermissionRepository;
import com.auth.service.auth_service.repository.IRoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RoleControllerTestInt {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IPermissionRepository permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/role - Should return all roles successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void getAllRoles_Success() throws Exception {
        Role role1 = new Role();
        role1.setRole("ADMIN");
        Role role2 = new Role();
        role2.setRole("USER");
        roleRepository.saveAll(List.of(role1, role2));

        mockMvc.perform(get("/api/role")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertThat(json).contains("ADMIN");
                    assertThat(json).contains("USER");
                });
    }

    @Test
    @DisplayName("GET /api/role/{id} - Should return a role by ID successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void getRoleById_Success() throws Exception {
        Role role = new Role();
        role.setRole("MANAGER");
        Role savedRole = roleRepository.save(role);

        mockMvc.perform(get("/api/role/{id}", savedRole.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRole.getId()))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    @DisplayName("GET /api/role/{id} - Should return 404 when role ID does not exist")
    @WithMockUser(roles = {"ADMIN"})
    public void getRoleById_NotFound() throws Exception {
        mockMvc.perform(get("/api/role/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Role not found."));
    }

    @Test
    @DisplayName("POST /api/role - Should save a role successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void saveRole_Success() throws Exception {
        Permission permission = new Permission();
        permission.setPermissionName("READ");
        Permission savedPermission = permissionRepository.save(permission);

        RoleRequestDTO requestDTO = new RoleRequestDTO();
        requestDTO.setRole("SUPERVISOR");
        requestDTO.setPermissionSet(new HashSet<>(Set.of(savedPermission)));

        mockMvc.perform(post("/api/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.role").value("SUPERVISOR"));

        Role roleDB = roleRepository.findAll().stream()
                .filter(r -> "SUPERVISOR".equals(r.getRole()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Role not found in DB"));

        assertThat(roleDB.getPermissionSet()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/role - Should return 400 when role validation fails")
    @WithMockUser(roles = {"ADMIN"})
    public void saveRole_ValidationFailure() throws Exception {
        RoleRequestDTO requestDTO = new RoleRequestDTO();
        requestDTO.setRole("A"); // Menor a 2 caracteres, rompe la validación @Size

        mockMvc.perform(post("/api/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    @DisplayName("PUT /api/role/{id} - Should update a role successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void updateRole_Success() throws Exception {
        Role role = new Role();
        role.setRole("OLD_ROLE");
        Role savedRole = roleRepository.save(role);

        RoleRequestDTO requestDTO = new RoleRequestDTO();
        requestDTO.setRole("NEW_ROLE");

        mockMvc.perform(put("/api/role/{id}", savedRole.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8) // o StandardCharsets.UTF_8
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRole.getId()))
                .andExpect(jsonPath("$.role").value("NEW_ROLE"));
    }

    @Test
    @DisplayName("DELETE /api/role/{id} - Should delete a role successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void deleteRole_Success() throws Exception {
        Role role = new Role();
        role.setRole("TO_DELETE");
        Role savedRole = roleRepository.save(role);

        mockMvc.perform(delete("/api/role/{id}", savedRole.getId()))
                .andExpect(status().isNoContent());

        assertThat(roleRepository.findById(savedRole.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/role/{id} - Should return 404 when deleting non-existent role")
    @WithMockUser(roles = {"ADMIN"})
    public void deleteRole_NotFound() throws Exception {
        mockMvc.perform(delete("/api/role/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Id not found."));
    }
}