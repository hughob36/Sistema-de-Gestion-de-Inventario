package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.UserAppRequestDTO;
import com.auth.service.auth_service.model.Role;
import com.auth.service.auth_service.model.UserApp;
import com.auth.service.auth_service.repository.IRoleRepository;
import com.auth.service.auth_service.repository.IUserAppRepository;
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
public class UserAppControllerTestInt {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserAppRepository userAppRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        userAppRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/user - Should return all users successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void getAllUsers_Success() throws Exception {
        UserApp user1 = new UserApp();
        user1.setName("User");
        user1.setLastname("One");
        user1.setDni("11111111");
        user1.setUsername("user1");
        user1.setPassword("password123");

        UserApp user2 = new UserApp();
        user2.setName("User");
        user2.setLastname("Two");
        user2.setDni("22222222");
        user2.setUsername("user2");
        user2.setPassword("password123");

        userAppRepository.saveAll(List.of(user1, user2));

        mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertThat(json).contains("user1");
                    assertThat(json).contains("user2");
                });
    }

    @Test
    @DisplayName("GET /api/user/{id} - Should return a user by ID successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void getUserById_Success() throws Exception {
        UserApp user = new UserApp();
        user.setName("Hugo");
        user.setLastname("Benítez");
        user.setDni("12345678");
        user.setUsername("hugo.benitez");
        user.setPassword("password123");
        UserApp savedUser = userAppRepository.save(user);

        mockMvc.perform(get("/api/user/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.username").value("hugo.benitez"));
    }

    @Test
    @DisplayName("GET /api/user/{id} - Should return 404 when user ID does not exist")
    @WithMockUser(roles = {"ADMIN"})
    public void getUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found."));
    }

    @Test
    @DisplayName("POST /api/user - Should save a user successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void saveUser_Success() throws Exception {
        Role role = new Role();
        role.setRole("ROLE_USER");
        Role savedRole = roleRepository.save(role);

        UserAppRequestDTO requestDTO = new UserAppRequestDTO();
        requestDTO.setName("Test");
        requestDTO.setLastname("User");
        requestDTO.setDni("87654321");
        requestDTO.setUsername("test.user");
        requestDTO.setPassword("password123");
        requestDTO.setEnable(true);
        requestDTO.setAccountNotExpired(true);
        requestDTO.setAccountNotLocked(true);
        requestDTO.setCredentialNotExpired(true);
        requestDTO.setRoleSet(new HashSet<>(Set.of(savedRole)));

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("test.user"));

        UserApp userDB = userAppRepository.findEntityUserAppByUsername("test.user")
                .orElseThrow(() -> new AssertionError("User not found in DB"));

        assertThat(userDB.getDni()).isEqualTo("87654321");
    }

    @Test
    @DisplayName("POST /api/user - Should return 400 when user validation fails (invalid DNI)")
    @WithMockUser(roles = {"ADMIN"})
    public void saveUser_ValidationFailure() throws Exception {
        UserAppRequestDTO requestDTO = new UserAppRequestDTO();
        requestDTO.setName("Test");
        requestDTO.setLastname("User");
        requestDTO.setDni("abc"); // Invalid DNI, violates pattern constraint
        requestDTO.setUsername("test.user");
        requestDTO.setPassword("password123");

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    @DisplayName("PUT /api/user/{id} - Should update a user successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void updateUser_Success() throws Exception {
        UserApp user = new UserApp();
        user.setName("OldName");
        user.setLastname("User");
        user.setDni("11223344");
        user.setUsername("old.username");
        user.setPassword("password123");
        UserApp savedUser = userAppRepository.save(user);

        UserAppRequestDTO requestDTO = new UserAppRequestDTO();
        requestDTO.setName("NewName");
        requestDTO.setLastname("User");
        requestDTO.setDni("11223344");
        requestDTO.setUsername("new.username");
        requestDTO.setPassword("newPassword123");
        requestDTO.setEnable(true);
        requestDTO.setAccountNotExpired(true);
        requestDTO.setAccountNotLocked(true);
        requestDTO.setCredentialNotExpired(true);

        mockMvc.perform(put("/api/user/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.username").value("new.username"));
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - Should delete a user successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void deleteUser_Success() throws Exception {
        UserApp user = new UserApp();
        user.setName("Delete");
        user.setLastname("Me");
        user.setDni("99887766");
        user.setUsername("delete.me");
        user.setPassword("password123");
        UserApp savedUser = userAppRepository.save(user);

        mockMvc.perform(delete("/api/user/{id}", savedUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(userAppRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - Should return 404 when deleting non-existent user")
    @WithMockUser(roles = {"ADMIN"})
    public void deleteUser_NotFound() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Id not exists."));
    }
}