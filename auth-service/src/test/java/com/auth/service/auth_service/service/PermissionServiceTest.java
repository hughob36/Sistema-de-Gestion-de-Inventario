package com.auth.service.auth_service.service;


import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.mapper.IPermissionMapper;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.repository.IPermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @Mock
    private IPermissionRepository permissionRepository;

    @Mock
    private IPermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    @DisplayName("Should return a list of PermissionResponseDTO when findAll is called")
    public void findAll_ShouldReturnPermissionResponseDTOList() {

        //Arrange
        Permission permission = new Permission(1L, "CREATE");
        List<Permission> permissionList = List.of(permission);

        PermissionResponseDTO responseDTO = new PermissionResponseDTO(1L, "CREATE");
        List<PermissionResponseDTO> expectedResponse = List.of(responseDTO);

        when(permissionRepository.findAll()).thenReturn(permissionList);
        when(permissionMapper.toPermissionResponseDTOList(permissionList)).thenReturn(expectedResponse);

        //Act
        List<PermissionResponseDTO> result = permissionService.findAll();
        //Assert
        assertAll("Verify response properties",
                () -> assertNotNull(result, "The result should not be null"),
                () -> assertEquals(1, result.size(), "The list size should be 1"),
                () -> assertEquals("CREATE", result.get(0).getPermissionName(), "The permission name does not match")
        );

        verify(permissionRepository, times(1)).findAll();
        verify(permissionMapper, times(1)).toPermissionResponseDTOList(anyList());
        verifyNoMoreInteractions(permissionRepository, permissionMapper);
    }

    @Test
    @DisplayName("Should return an empty list when no permissions exist in the database")
    public void findAll_ShouldReturnEmptyPermissionResponseDTOList() {
        // Arrange
        List<Permission> emptyPermissionList = List.of();
        List<PermissionResponseDTO> emptyResponseDTOList = List.of();

        when(permissionRepository.findAll()).thenReturn(emptyPermissionList);
        when(permissionMapper.toPermissionResponseDTOList(emptyPermissionList)).thenReturn(emptyResponseDTOList);
        // Act
        List<PermissionResponseDTO> result = permissionService.findAll();
        // Assert
        assertAll("Verify empty response properties",
                () -> assertNotNull(result, "The result should not be null even if empty"),
                () -> assertTrue(result.isEmpty(), "The list should be empty"),
                () -> assertEquals(0, result.size(), "The size of the list should be 0")
        );
        verify(permissionRepository, times(1)).findAll();
        verify(permissionMapper, times(1)).toPermissionResponseDTOList(emptyPermissionList);
    }



}
