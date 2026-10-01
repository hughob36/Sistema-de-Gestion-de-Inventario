package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.UserAppRequestDTO;
import com.auth.service.auth_service.dto.UserAppResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.security.config.SecurityConfig;
import com.auth.service.auth_service.service.IUserAppService;
import com.auth.service.auth_service.service.UserDetailsServiceImpl;
import com.auth.service.auth_service.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAppController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class UserAppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserAppService userAppService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private UserAppRequestDTO validRequestDTO;
    private UserAppResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        validRequestDTO = new UserAppRequestDTO();
        validRequestDTO.setName("Hugo");
        validRequestDTO.setLastname("Benítez");
        validRequestDTO.setDni("12345678");
        validRequestDTO.setUsername("hugo.benitez");
        validRequestDTO.setPassword("password123");
        validRequestDTO.setEnable(true);
        validRequestDTO.setAccountNotExpired(true);
        validRequestDTO.setAccountNotLocked(true);
        validRequestDTO.setCredentialNotExpired(true);
        validRequestDTO.setRoleSet(new HashSet<>());

        sampleResponseDTO = new UserAppResponseDTO();
        sampleResponseDTO.setId(1L);
        sampleResponseDTO.setName("Hugo");
        sampleResponseDTO.setLastname("Benítez");
        sampleResponseDTO.setDni("12345678");
        sampleResponseDTO.setUsername("hugo.benitez");
        sampleResponseDTO.setPassword("encodedPassword123");
        sampleResponseDTO.setEnable(true);
        sampleResponseDTO.setAccountNotExpired(true);
        sampleResponseDTO.setAccountNotLocked(true);
        sampleResponseDTO.setCredentialNotExpired(true);
        sampleResponseDTO.setRoleSet(new HashSet<>());
    }

    // ==========================================
    // GET /api/user
    // ==========================================
    @Nested
    @DisplayName("GET /api/user")
    class GetAllUsersTests {

        @Test
        @DisplayName("Positive: Should return 200 OK and the list of users")
        void getAllUsers_WhenUsersExist_ShouldReturnOk() throws Exception {
            when(userAppService.findAll()).thenReturn(List.of(sampleResponseDTO));

            mockMvc.perform(get("/api/user")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].username").value("hugo.benitez"));

            verify(userAppService, times(1)).findAll();
        }

        @Test
        @DisplayName("Positive: Should return 200 OK and an empty list when no records exist")
        void getAllUsers_WhenEmpty_ShouldReturnOkEmptyList() throws Exception {
            when(userAppService.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/user")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(userAppService, times(1)).findAll();
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        @DisplayName("Negative: Should return 401 Unauthorized if the user is not authenticated")
        void getAllUsers_WhenAnonymous_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/user"))
                    .andExpect(status().isUnauthorized());

            verify(userAppService, never()).findAll();
        }
    }

    // ==========================================
    // GET /api/user/{id}
    // ==========================================
    @Nested
    @DisplayName("GET /api/user/{id}")
    class GetUserByIdTests {

        @Test
        @DisplayName("Positive: Should return 200 OK and the user when ID exists")
        void getUserById_WhenIdExists_ShouldReturnOk() throws Exception {
            Long userId = 1L;
            when(userAppService.findById(userId)).thenReturn(sampleResponseDTO);

            mockMvc.perform(get("/api/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.username").value("hugo.benitez"));

            verify(userAppService, times(1)).findById(userId);
        }

        @Test
        @DisplayName("Negative: Should return 404 Not Found when ID does not exist")
        void getUserById_WhenIdNotFound_ShouldReturnNotFound() throws Exception {
            Long nonExistentId = 999L;
            when(userAppService.findById(nonExistentId)).thenThrow(new ResourceNotFoundException("User not found."));

            mockMvc.perform(get("/api/user/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(userAppService, times(1)).findById(nonExistentId);
        }
    }

    // ==========================================
    // POST /api/user
    // ==========================================
    @Nested
    @DisplayName("POST /api/user")
    class CreateUserTests {

        @Test
        @DisplayName("Positive: Should return 201 Created when body is valid")
        void createUser_WhenValidBody_ShouldReturnCreated() throws Exception {
            when(userAppService.save(any(UserAppRequestDTO.class))).thenReturn(sampleResponseDTO);

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.username").value("hugo.benitez"));

            verify(userAppService, times(1)).save(any(UserAppRequestDTO.class));
        }

        @Test
        @DisplayName("Negative: Should return 400 Bad Request when DNI is invalid (contains letters)")
        void createUser_WhenInvalidDni_ShouldReturnBadRequest() throws Exception {
            validRequestDTO.setDni("1234abc");

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isBadRequest());

            verify(userAppService, never()).save(any(UserAppRequestDTO.class));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Negative: Should return 401 Unauthorized when making POST without authentication")
        void createUser_WhenAnonymous_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isUnauthorized());

            verify(userAppService, never()).save(any(UserAppRequestDTO.class));
        }
    }

    // ==========================================
    // PUT /api/user/{id}
    // ==========================================
    @Nested
    @DisplayName("PUT /api/user/{id}")
    class UpdateUserTests {

        @Test
        @DisplayName("Positive: Should return 200 OK and updated user when ID and body are valid")
        void updateUserById_WhenValidRequest_ShouldReturnOk() throws Exception {
            Long userId = 1L;
            when(userAppService.updateById(eq(userId), any(UserAppRequestDTO.class))).thenReturn(sampleResponseDTO);

            mockMvc.perform(put("/api/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            verify(userAppService, times(1)).updateById(eq(userId), any(UserAppRequestDTO.class));
        }

        @Test
        @DisplayName("Negative: Should return 404 Not Found when ID to update does not exist")
        void updateUserById_WhenIdNotFound_ShouldReturnNotFound() throws Exception {
            Long nonExistentId = 999L;
            when(userAppService.updateById(eq(nonExistentId), any(UserAppRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("User not found."));

            mockMvc.perform(put("/api/user/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isNotFound());

            verify(userAppService, times(1)).updateById(eq(nonExistentId), any(UserAppRequestDTO.class));
        }
    }

    // ==========================================
    // DELETE /api/user/{id}
    // ==========================================
    @Nested
    @DisplayName("DELETE /api/user/{id}")
    class DeleteUserTests {

        @Test
        @DisplayName("Positive: Should return 204 No Content when deleted successfully")
        void deleteUserById_WhenIdExists_ShouldReturnNoContent() throws Exception {
            Long userId = 1L;
            doNothing().when(userAppService).deleteById(userId);

            mockMvc.perform(delete("/api/user/{id}", userId))
                    .andExpect(status().isNoContent());

            verify(userAppService, times(1)).deleteById(userId);
        }

        @Test
        @DisplayName("Negative: Should return 404 Not Found when ID to delete does not exist")
        void deleteUserById_WhenIdNotFound_ShouldReturnNotFound() throws Exception {
            Long nonExistentId = 999L;
            doThrow(new ResourceNotFoundException("Id not exists.")).when(userAppService).deleteById(nonExistentId);

            mockMvc.perform(delete("/api/user/{id}", nonExistentId))
                    .andExpect(status().isNotFound());

            verify(userAppService, times(1)).deleteById(nonExistentId);
        }
    }
}