package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.repository.IPermissionRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PermissionControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IPermissionRepository permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void cleanDatabase() {
        /*userAppRepository.deleteAll();
        roleRepository.deleteAll();*/
        permissionRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/permission - Should return all permissions for ADMIN")
    @WithMockUser(roles = {"ADMIN"})
    public void getAllPermissions_Success() throws Exception {

        Permission p1 = new Permission();
        p1.setPermissionName("CREATE");
        Permission p2 = new Permission();
        p2.setPermissionName("UPDATE");
        permissionRepository.saveAll(List.of(p1, p2));

        // 2. Act & Assert
        mockMvc.perform(get("/api/permission")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertThat(json).contains("CREATE");
                    assertThat(json).contains("UPDATE");
                });
    }

    @Test
    @DisplayName("POST /api/permission - Should save a permission successfully")
    @WithMockUser(roles = {"ADMIN"})
    public void save_Permission() throws Exception {

        Permission permissionRequest = new Permission();
        permissionRequest.setPermissionName("CREATE");

        MvcResult mvcResult = mockMvc.perform(post("/api/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(permissionRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // 3. Assert
        Permission permissionDB = permissionRepository.findAll().stream()
                .filter(p -> "CREATE".equals(p.getPermissionName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Permission 'CREATE' not found in database"));

        assertThat(permissionDB.getId()).isNotNull();
        assertThat(permissionDB.getPermissionName()).isEqualTo("CREATE");
    }
}
