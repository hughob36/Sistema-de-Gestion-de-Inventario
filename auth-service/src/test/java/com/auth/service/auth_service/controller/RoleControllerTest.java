package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.RoleRequestDTO;
import com.auth.service.auth_service.dto.RoleResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.security.config.SecurityConfig;
import com.auth.service.auth_service.service.IRoleService;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRoleService roleService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private RoleRequestDTO validRequestDTO;
    private RoleResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        // Inicializa con datos válidos según los atributos de tus DTOs
        validRequestDTO = new RoleRequestDTO("ADMIN",new HashSet<>());
        sampleResponseDTO = new RoleResponseDTO(99L,"ADMIN",new HashSet<>());
    }

    // ==========================================
    // GET /api/role
    // ==========================================
    @Nested
    @DisplayName("GET /api/role")
    class GetAllRolesTests {

        @Test
        @DisplayName("Positivo: Debe retornar 200 OK y la lista con roles")
        void getAllRole_WhenRolesExist_ShouldReturnOk() throws Exception {
            when(roleService.findAll()).thenReturn(List.of(sampleResponseDTO));

            mockMvc.perform(get("/api/role")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(1));

            verify(roleService, times(1)).findAll();
        }

        @Test
        @DisplayName("Positivo: Debe retornar 200 OK y una lista vacía cuando no hay registros")
        void getAllRole_WhenEmpty_ShouldReturnOkEmptyList() throws Exception {
            when(roleService.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/role")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(roleService, times(1)).findAll();
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Negativo: Debe retornar 401 Unauthorized si el usuario no está autenticado")
        void getAllRole_WhenAnonymous_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/api/role"))
                    .andExpect(status().isUnauthorized());

            verify(roleService, never()).findAll();
        }
    }

    // ==========================================
    // GET /api/role/{id}
    // ==========================================
    @Nested
    @DisplayName("GET /api/role/{id}")
    class GetRoleByIdTests {

        @Test
        @DisplayName("Positivo: Debe retornar 200 OK y el rol cuando el ID existe")
        void getRoleById_WhenIdExists_ShouldReturnOk() throws Exception {
            Long roleId = 99L;
            when(roleService.findById(roleId)).thenReturn(sampleResponseDTO);

            mockMvc.perform(get("/api/role/{id}", roleId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            assertEquals(roleId,sampleResponseDTO.getId());
            verify(roleService, times(1)).findById(roleId);
        }

        @Test
        @DisplayName("Negativo: Debe retornar 404 Not Found cuando el ID no existe")
        void getRoleById_WhenIdNotFound_ShouldReturnNotFound() throws Exception {
            Long nonExistentId = 99L;
            when(roleService.findById(nonExistentId)).thenThrow(new ResourceNotFoundException("Role not found."));

            mockMvc.perform(get("/api/role/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(roleService, times(1)).findById(nonExistentId);
        }
    }

    // ==========================================
    // POST /api/role
    // ==========================================
    @Nested
    @DisplayName("POST /api/role")
    class CreateRoleTests {

        @Test
        @DisplayName("Positivo: Debe retornar 201 Created cuando el body es válido")
        void createRole_WhenValidBody_ShouldReturnCreated() throws Exception {
            when(roleService.save(any(RoleRequestDTO.class))).thenReturn(sampleResponseDTO);

            mockMvc.perform(post("/api/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            verify(roleService, times(1)).save(any(RoleRequestDTO.class));
        }

        @Test
        //@WithMockUser(username = "user", roles = {"USER"})
        @WithAnonymousUser
        @DisplayName("Negativo: Debe retornar 403 Forbidden al hacer POST.")
        void createRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(post("/api/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isUnauthorized());

            verify(roleService, never()).save(any(RoleRequestDTO.class));
        }
    }

    // ==========================================
    // PUT /api/role/{id}
    // ==========================================
    @Nested
    @DisplayName("PUT /api/role/{id}")
    class UpdateRoleTests {

        @Test
        @DisplayName("Positivo: Debe retornar 200 OK y el rol actualizado cuando el ID y body son válidos")
        void updateRoleById_WhenValidRequest_ShouldReturnOk() throws Exception {
            Long roleId = 99L;
            when(roleService.updateById(eq(roleId), any(RoleRequestDTO.class))).thenReturn(sampleResponseDTO);

            mockMvc.perform(put("/api/role/{id}", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            verify(roleService, times(1)).updateById(eq(roleId), any(RoleRequestDTO.class));
        }

        @Test
        @DisplayName("Negativo: Debe retornar 404 Not Found cuando el ID a actualizar no existe")
        void updateRoleById_WhenIdNotFound_ShouldReturnNotFound() throws Exception {
            Long nonExistentId = 99L;
            when(roleService.updateById(eq(nonExistentId), any(RoleRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Role not found."));

            mockMvc.perform(put("/api/role/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestDTO)))
                    .andExpect(status().isNotFound());

            verify(roleService, times(1)).updateById(eq(nonExistentId), any(RoleRequestDTO.class));
        }

    }

    // ==========================================
    // DELETE /api/role/{id}
    // ==========================================
    @Nested
    @DisplayName("DELETE /api/role/{id}")
    class DeleteRoleTests {

        @Test
        @DisplayName("Positivo: Debe retornar 204 No Content cuando se elimina correctamente")
        void deleteRoleById_WhenIdExists_ShouldReturnNoContent() throws Exception {
            Long roleId = 1L;
            doNothing().when(roleService).deleteById(roleId);

            mockMvc.perform(delete("/api/role/{id}", roleId))
                    .andExpect(status().isNoContent());

            verify(roleService, times(1)).deleteById(roleId);
        }

        @Test
        @DisplayName("Negativo: Debe retornar 404 Not Found cuando el ID a eliminar no existe")
        void deleteRoleById_WhenIdNotFound_ShouldReturnNotFound() throws Exception {
            Long nonExistentId = 99L;
            doThrow(new ResourceNotFoundException("Id not found.")).when(roleService).deleteById(nonExistentId);

            mockMvc.perform(delete("/api/role/{id}", nonExistentId))
                    .andExpect(status().isNotFound());

            verify(roleService, times(1)).deleteById(nonExistentId);
        }
    }
}