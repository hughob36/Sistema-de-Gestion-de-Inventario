package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.PermissionRequestDTO;
import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.service.IPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPermissionService permissionService;

    @Autowired
    private ObjectMapper objectMapper;

    // ==========================================
    // GET /api/permission
    // ==========================================
    @Nested
    @DisplayName("GET /api/permission")
    class GetAllPermissions {

        @Test
        @DisplayName("Positivo: Debe retornar lista de permisos con 200 OK cuando existen datos")
        void getAllPermission_ShouldReturnPermissionList_WhenDataExists() throws Exception {
            PermissionResponseDTO dto = new PermissionResponseDTO(1L, "CREATE");
            when(permissionService.findAll()).thenReturn(List.of(dto));

            mockMvc.perform(get("/api/permission").accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].permissionName").value("CREATE"));

            verify(permissionService, times(1)).findAll();
        }

        @Test
        @DisplayName("Positivo: Debe retornar lista vacía con 200 OK cuando no existen registros")
        void getAllPermission_ShouldReturnEmptyList_WhenNoData() throws Exception {
            when(permissionService.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/permission").accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(permissionService, times(1)).findAll();
        }
    }

    // ==========================================
    // GET /api/permission/{id}
    // ==========================================
    @Nested
    @DisplayName("GET /api/permission/{id}")
    class GetPermissionById {

        @Test
        @DisplayName("Positivo: Debe retornar el permiso correspondiente con 200 OK")
        void getPermissionById_ShouldReturnPermission_WhenIdExists() throws Exception {
            PermissionResponseDTO dto = new PermissionResponseDTO(1L, "READ");
            when(permissionService.findById(1L)).thenReturn(dto);

            mockMvc.perform(get("/api/permission/1").accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.permissionName").value("READ"));

            verify(permissionService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Negativo: Debe retornar 404 Not Found cuando el ID no existe")
        void getPermissionById_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
            when(permissionService.findById(99L)).thenThrow(new ResourceNotFoundException("Resource not found."));

            mockMvc.perform(get("/api/permission/99").accept(APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(permissionService, times(1)).findById(99L);
        }
    }

    // ==========================================
    // POST /api/permission
    // ==========================================
    @Nested
    @DisplayName("POST /api/permission")
    class CreatePermission {

        @Test
        @DisplayName("Positivo: Debe crear un nuevo permiso y retornar 201 Created")
        void createPermission_ShouldReturn201_WhenRequestIsValid() throws Exception {
            PermissionRequestDTO requestDTO = new PermissionRequestDTO("DELETE");
            PermissionResponseDTO responseDTO = new PermissionResponseDTO(1L, "DELETE");

            when(permissionService.save(any(PermissionRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/permission")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.permissionName").value("DELETE"));

            verify(permissionService, times(1)).save(any(PermissionRequestDTO.class));
        }

        @Test
        @DisplayName("Negativo: Debe retornar 400 Bad Request cuando el body es inválido (@Valid)")
        void createPermission_ShouldReturn400_WhenRequestIsInvalid() throws Exception {
            // Suponiendo que permissionName no puede ser blank/null en el DTO
            PermissionRequestDTO invalidDTO = new PermissionRequestDTO("");

            mockMvc.perform(post("/api/permission")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(permissionService, never()).save(any());
        }
    }

    // ==========================================
    // PUT /api/permission/{id}
    // ==========================================
    @Nested
    @DisplayName("PUT /api/permission/{id}")
    class UpdatePermission {

        @Test
        @DisplayName("Positivo: Debe actualizar el permiso y retornar 200 OK")
        void updatePermission_ShouldReturn200_WhenIdAndRequestAreValid() throws Exception {
            PermissionRequestDTO requestDTO = new PermissionRequestDTO("UPDATE_ROLE");
            PermissionResponseDTO responseDTO = new PermissionResponseDTO(1L, "UPDATE_ROLE");

            when(permissionService.updateById(eq(1L), any(PermissionRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/api/permission/1")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.permissionName").value("UPDATE_ROLE"));

            verify(permissionService, times(1)).updateById(eq(1L), any(PermissionRequestDTO.class));
        }

        @Test
        @DisplayName("Negativo: Debe retornar 404 Not Found cuando se intenta actualizar un ID inexistente")
        void updatePermission_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
            PermissionRequestDTO requestDTO = new PermissionRequestDTO("UPDATE_ROLE");

            when(permissionService.updateById(eq(99L), any(PermissionRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Resource not found."));

            mockMvc.perform(put("/api/permission/99")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());

            verify(permissionService, times(1)).updateById(eq(99L), any(PermissionRequestDTO.class));
        }

        @Test
        @DisplayName("Negativo: Debe retornar 400 Bad Request al enviar datos inválidos en el Body")
        void updatePermission_ShouldReturn400_WhenRequestBodyIsInvalid() throws Exception {
            PermissionRequestDTO invalidDTO = new PermissionRequestDTO(null);

            mockMvc.perform(put("/api/permission/1")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(permissionService, never()).updateById(anyLong(), any());
        }
    }

    // ==========================================
    // DELETE /api/permission/{id}
    // ==========================================
    @Nested
    @DisplayName("DELETE /api/permission/{id}")
    class DeletePermission {

        @Test
        @DisplayName("Positivo: Debe eliminar el permiso y retornar 204 No Content")
        void deletePermission_ShouldReturn204_WhenIdExists() throws Exception {
            doNothing().when(permissionService).deleteById(1L);

            mockMvc.perform(delete("/api/permission/1").with(csrf()))
                    .andExpect(status().isNoContent());

            verify(permissionService, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Negativo: Debe retornar 404 Not Found cuando el ID a eliminar no existe")
        void deletePermission_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
            doThrow(new ResourceNotFoundException("Resource not exists.")).when(permissionService).deleteById(99L);

            mockMvc.perform(delete("/api/permission/99").with(csrf()))
                    .andExpect(status().isNotFound());

            verify(permissionService, times(1)).deleteById(99L);
        }
    }
}