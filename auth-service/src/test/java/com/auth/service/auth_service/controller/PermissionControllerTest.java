package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.service.IPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PermissionController.class)
public class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPermissionService permissionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/permission - Should return list of permissions with 200 OK when data exists")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getAllPermission_ShouldReturnPermissionResponseDTOList() throws Exception {
        // 1. Arrange
        PermissionResponseDTO dto = new PermissionResponseDTO(1L, "CREATE");
        List<PermissionResponseDTO> dtoList = List.of(dto);

        when(permissionService.findAll()).thenReturn(dtoList);

        // 2. Act  & 3. Assert
        mockMvc.perform(get("/api/permission").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].permissionName").value("CREATE"))
                .andExpect(jsonPath("$[0].permissionName").exists());

        verify(permissionService, times(1)).findAll();
    }
}
