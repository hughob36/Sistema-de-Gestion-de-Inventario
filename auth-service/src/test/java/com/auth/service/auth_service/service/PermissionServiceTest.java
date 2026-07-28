package com.auth.service.auth_service.service;

import com.auth.service.auth_service.dto.PermissionRequestDTO;
import com.auth.service.auth_service.dto.PermissionResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.mapper.IPermissionMapper;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.repository.IPermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @Mock
    private IPermissionRepository permissionRepository;

    @Mock
    private IPermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return a list of PermissionResponseDTO when permissions exist")
        void findAll_ShouldReturnPermissionResponseDTOList() {
            // Arrange
            Permission permission = new Permission(1L, "CREATE");
            List<Permission> permissionList = List.of(permission);

            PermissionResponseDTO responseDTO = new PermissionResponseDTO(1L, "CREATE");
            List<PermissionResponseDTO> expectedResponse = List.of(responseDTO);

            when(permissionRepository.findAll()).thenReturn(permissionList);
            when(permissionMapper.toPermissionResponseDTOList(permissionList)).thenReturn(expectedResponse);

            // Act
            List<PermissionResponseDTO> result = permissionService.findAll();

            // Assert
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
        void findAll_ShouldReturnEmptyList_WhenNoPermissionsExist() {
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
                    () -> assertTrue(result.isEmpty(), "The list should be empty")
            );

            verify(permissionRepository, times(1)).findAll();
            verify(permissionMapper, times(1)).toPermissionResponseDTOList(emptyPermissionList);
            verifyNoMoreInteractions(permissionRepository, permissionMapper);
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return a PermissionResponseDTO when ID exists")
        void findById_ShouldReturnPermissionResponseDTO_WhenIdExists() {
            // Arrange
            Long id = 1L;
            Permission permission = new Permission(id, "CREATE");
            PermissionResponseDTO permissionResponseDTO = new PermissionResponseDTO(id, "CREATE");

            when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
            when(permissionMapper.toPermissionResponseDTO(permission)).thenReturn(permissionResponseDTO);

            // Act
            PermissionResponseDTO result = permissionService.findById(id);

            // Assert
            assertAll("Verify response properties",
                    () -> assertNotNull(result, "The result should not be null"),
                    () -> assertEquals("CREATE", result.getPermissionName(), "The permission name does not match"),
                    () -> assertEquals(id, result.getId(), "The ID does not match")
            );

            verify(permissionRepository, times(1)).findById(id);
            verify(permissionMapper, times(1)).toPermissionResponseDTO(permission);
            verifyNoMoreInteractions(permissionRepository, permissionMapper);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void findById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
            // Arrange
            Long id = 99L;
            when(permissionRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                    permissionService.findById(id)
            );

            assertEquals("Id '" + id + "' not found.", exception.getMessage());
            verify(permissionRepository, times(1)).findById(id);
            verifyNoInteractions(permissionMapper);
        }
    }

    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("Should save and return PermissionResponseDTO when request is valid")
        void save_ShouldReturnPermissionResponseDTO_WhenRequestIsValid() {
            // Arrange
            PermissionRequestDTO requestDTO = new PermissionRequestDTO("READ");
            Permission permissionToSave = new Permission(null, "READ");
            Permission savedPermission = new Permission(1L, "READ");
            PermissionResponseDTO expectedResponseDTO = new PermissionResponseDTO(1L, "READ");

            when(permissionMapper.toPermission(requestDTO)).thenReturn(permissionToSave);
            when(permissionRepository.save(permissionToSave)).thenReturn(savedPermission);
            when(permissionMapper.toPermissionResponseDTO(savedPermission)).thenReturn(expectedResponseDTO);

            // Act
            PermissionResponseDTO result = permissionService.save(requestDTO);

            // Assert
            assertAll("Verify saved permission response",
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(1L, result.getId(), "ID should match saved entity ID"),
                    () -> assertEquals("READ", result.getPermissionName(), "Permission name should match")
            );

            verify(permissionMapper, times(1)).toPermission(requestDTO);
            verify(permissionRepository, times(1)).save(permissionToSave);
            verify(permissionMapper, times(1)).toPermissionResponseDTO(savedPermission);
            verifyNoMoreInteractions(permissionRepository, permissionMapper);
        }
    }

    @Nested
    @DisplayName("deleteById Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete permission successfully when ID exists")
        void deleteById_ShouldDelete_WhenIdExists() {
            // Arrange
            Long id = 1L;
            when(permissionRepository.existsById(id)).thenReturn(true);
            doNothing().when(permissionRepository).deleteById(id);

            // Act & Assert
            assertDoesNotThrow(() -> permissionService.deleteById(id));

            verify(permissionRepository, times(1)).existsById(id);
            verify(permissionRepository, times(1)).deleteById(id);
            verifyNoInteractions(permissionMapper);
            verifyNoMoreInteractions(permissionRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent ID")
        void deleteById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
            // Arrange
            Long id = 99L;
            when(permissionRepository.existsById(id)).thenReturn(false);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                    permissionService.deleteById(id)
            );

            assertEquals("Resource not exists.", exception.getMessage());
            verify(permissionRepository, times(1)).existsById(id);
            verify(permissionRepository, never()).deleteById(anyLong());
            verifyNoInteractions(permissionMapper);
        }
    }

    @Nested
    @DisplayName("updateById Tests")
    class UpdateByIdTests {

        @Test
        @DisplayName("Should update and return PermissionResponseDTO when ID exists")
        void updateById_ShouldReturnUpdatedPermissionResponseDTO_WhenIdExists() {
            // Arrange
            Long id = 1L;
            PermissionRequestDTO requestDTO = new PermissionRequestDTO("UPDATE_ROLE");
            Permission existingPermission = new Permission(id, "OLD_ROLE");
            Permission updatedPermission = new Permission(id, "UPDATE_ROLE");
            PermissionResponseDTO expectedResponseDTO = new PermissionResponseDTO(id, "UPDATE_ROLE");

            when(permissionRepository.findById(id)).thenReturn(Optional.of(existingPermission));
            doNothing().when(permissionMapper).updatePermissionFromDto(requestDTO, existingPermission);
            when(permissionRepository.save(existingPermission)).thenReturn(updatedPermission);
            when(permissionMapper.toPermissionResponseDTO(updatedPermission)).thenReturn(expectedResponseDTO);

            // Act
            PermissionResponseDTO result = permissionService.updateById(id, requestDTO);

            // Assert
            assertAll("Verify updated permission response",
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(id, result.getId(), "ID should remain the same"),
                    () -> assertEquals("UPDATE_ROLE", result.getPermissionName(), "Permission name should be updated")
            );

            verify(permissionRepository, times(1)).findById(id);
            verify(permissionMapper, times(1)).updatePermissionFromDto(requestDTO, existingPermission);
            verify(permissionRepository, times(1)).save(existingPermission);
            verify(permissionMapper, times(1)).toPermissionResponseDTO(updatedPermission);
            verifyNoMoreInteractions(permissionRepository, permissionMapper);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent ID")
        void updateById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
            // Arrange
            Long id = 99L;
            PermissionRequestDTO requestDTO = new PermissionRequestDTO("UPDATE_ROLE");
            when(permissionRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                    permissionService.updateById(id, requestDTO)
            );

            assertEquals("Resource not found.", exception.getMessage());
            verify(permissionRepository, times(1)).findById(id);
            verify(permissionRepository, never()).save(any());
            verifyNoInteractions(permissionMapper);
        }
    }
}