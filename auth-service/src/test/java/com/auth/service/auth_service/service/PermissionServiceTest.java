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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionService Unit Tests")
class PermissionServiceTest {

    @Mock
    private IPermissionRepository permissionRepository;

    @Mock
    private IPermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    // =========================================================================
    // findAll() Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for findAll()")
    class FindAllTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should return list of PermissionResponseDTO when permissions exist")
            void shouldReturnPermissionListWhenEntitiesExist() {
                // Arrange
                Permission perm1 = new Permission(1L, "READ_PRIVILEGE");
                Permission perm2 = new Permission(2L, "WRITE_PRIVILEGE");
                List<Permission> permissionEntities = List.of(perm1, perm2);

                PermissionResponseDTO dto1 = new PermissionResponseDTO(1L, "READ_PRIVILEGE");
                PermissionResponseDTO dto2 = new PermissionResponseDTO(2L, "WRITE_PRIVILEGE");
                List<PermissionResponseDTO> expectedResponse = List.of(dto1, dto2);

                when(permissionRepository.findAll()).thenReturn(permissionEntities);
                when(permissionMapper.toPermissionResponseDTOList(permissionEntities)).thenReturn(expectedResponse);

                // Act
                List<PermissionResponseDTO> actualResponse = permissionService.findAll();

                // Assert
                assertAll("Verify elements in returned list",
                        () -> assertNotNull(actualResponse, "Response list must not be null"),
                        () -> assertEquals(2, actualResponse.size(), "List must contain 2 elements"),
                        () -> assertEquals("READ_PRIVILEGE", actualResponse.get(0).getPermissionName()),
                        () -> assertEquals("WRITE_PRIVILEGE", actualResponse.get(1).getPermissionName())
                );

                verify(permissionRepository, times(1)).findAll();
                verify(permissionMapper, times(1)).toPermissionResponseDTOList(permissionEntities);
                verifyNoMoreInteractions(permissionRepository, permissionMapper);
            }

            @Test
            @DisplayName("Should return empty list when no records exist")
            void shouldReturnEmptyListWhenDatabaseIsEmpty() {
                // Arrange
                when(permissionRepository.findAll()).thenReturn(Collections.emptyList());
                when(permissionMapper.toPermissionResponseDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

                // Act
                List<PermissionResponseDTO> actualResponse = permissionService.findAll();

                // Assert
                assertAll("Verify empty list response",
                        () -> assertNotNull(actualResponse, "Empty result should be non-null"),
                        () -> assertTrue(actualResponse.isEmpty(), "Result list should be empty")
                );

                verify(permissionRepository, times(1)).findAll();
                verify(permissionMapper, times(1)).toPermissionResponseDTOList(Collections.emptyList());
                verifyNoMoreInteractions(permissionRepository, permissionMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should propagate unexpected RuntimeException when repository fails")
            void shouldPropagateExceptionWhenRepositoryFails() {
                // Arrange
                when(permissionRepository.findAll()).thenThrow(new RuntimeException("Database connection timeout"));

                // Act & Assert
                RuntimeException ex = assertThrows(RuntimeException.class, () -> permissionService.findAll());
                assertEquals("Database connection timeout", ex.getMessage());

                verify(permissionRepository, times(1)).findAll();
                verifyNoInteractions(permissionMapper);
            }
        }
    }

    // =========================================================================
    // findById(Long id) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for findById(Long id)")
    class FindByIdTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should return PermissionResponseDTO when valid ID is provided")
            void shouldReturnDtoWhenIdExists() {
                // Arrange
                Long targetId = 10L;
                Permission permission = new Permission(targetId, "DELETE_PRIVILEGE");
                PermissionResponseDTO expectedDto = new PermissionResponseDTO(targetId, "DELETE_PRIVILEGE");

                when(permissionRepository.findById(targetId)).thenReturn(Optional.of(permission));
                when(permissionMapper.toPermissionResponseDTO(permission)).thenReturn(expectedDto);

                // Act
                PermissionResponseDTO actualDto = permissionService.findById(targetId);

                // Assert
                assertAll("Verify found entity properties",
                        () -> assertNotNull(actualDto, "Response should not be null"),
                        () -> assertEquals(targetId, actualDto.getId()),
                        () -> assertEquals("DELETE_PRIVILEGE", actualDto.getPermissionName())
                );

                verify(permissionRepository, times(1)).findById(targetId);
                verify(permissionMapper, times(1)).toPermissionResponseDTO(permission);
                verifyNoMoreInteractions(permissionRepository, permissionMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
            void shouldThrowResourceNotFoundExceptionWhenIdNotFound() {
                // Arrange
                Long nonExistentId = 999L;
                when(permissionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> permissionService.findById(nonExistentId)
                );

                assertEquals("Permission with id '" + nonExistentId + "' not found.", exception.getMessage());
                verify(permissionRepository, times(1)).findById(nonExistentId);
                verifyNoInteractions(permissionMapper);
            }

            @Test
            @DisplayName("Should throw ResourceNotFoundException when id is null")
            void shouldThrowResourceNotFoundExceptionWhenIdIsNull() {
                // Arrange
                when(permissionRepository.findById(null)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> permissionService.findById(null)
                );

                assertEquals("Permission with id 'null' not found.", exception.getMessage());
                verify(permissionRepository, times(1)).findById(null);
                verifyNoInteractions(permissionMapper);
            }
        }
    }

    // =========================================================================
    // save(PermissionRequestDTO dto) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for save(PermissionRequestDTO dto)")
    class SaveTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should persist and return PermissionResponseDTO on valid request")
            void shouldSaveAndReturnDtoWhenRequestIsValid() {
                // Arrange
                PermissionRequestDTO requestDTO = new PermissionRequestDTO("CREATE_USER");
                Permission unpersistedEntity = new Permission(null, "CREATE_USER");
                Permission persistedEntity = new Permission(1L, "CREATE_USER");
                PermissionResponseDTO expectedResponse = new PermissionResponseDTO(1L, "CREATE_USER");

                when(permissionMapper.toPermission(requestDTO)).thenReturn(unpersistedEntity);
                when(permissionRepository.save(unpersistedEntity)).thenReturn(persistedEntity);
                when(permissionMapper.toPermissionResponseDTO(persistedEntity)).thenReturn(expectedResponse);

                // Act
                PermissionResponseDTO actualResponse = permissionService.save(requestDTO);

                // Assert
                assertAll("Verify saved entity details",
                        () -> assertNotNull(actualResponse),
                        () -> assertEquals(1L, actualResponse.getId()),
                        () -> assertEquals("CREATE_USER", actualResponse.getPermissionName())
                );

                verify(permissionMapper, times(1)).toPermission(requestDTO);
                verify(permissionRepository, times(1)).save(unpersistedEntity);
                verify(permissionMapper, times(1)).toPermissionResponseDTO(persistedEntity);
                verifyNoMoreInteractions(permissionRepository, permissionMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should propagate DataIntegrityViolationException when permission name already exists")
            void shouldThrowDataIntegrityViolationExceptionWhenDuplicateUniqueConstraintTriggered() {
                // Arrange
                PermissionRequestDTO requestDTO = new PermissionRequestDTO("ALREADY_EXISTS");
                Permission entity = new Permission(null, "ALREADY_EXISTS");

                when(permissionMapper.toPermission(requestDTO)).thenReturn(entity);
                when(permissionRepository.save(entity))
                        .thenThrow(new DataIntegrityViolationException("Unique constraint violation: duplicate permission name"));

                // Act & Assert
                assertThrows(DataIntegrityViolationException.class, () -> permissionService.save(requestDTO));

                verify(permissionMapper, times(1)).toPermission(requestDTO);
                verify(permissionRepository, times(1)).save(entity);
                verify(permissionMapper, never()).toPermissionResponseDTO(any());
            }
        }
    }

    // =========================================================================
    // deleteById(Long id) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for deleteById(Long id)")
    class DeleteByIdTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should execute repository delete when ID exists")
            void shouldDeleteWhenIdExists() {
                // Arrange
                Long targetId = 5L;
                when(permissionRepository.existsById(targetId)).thenReturn(true);
                doNothing().when(permissionRepository).deleteById(targetId);

                // Act & Assert
                assertDoesNotThrow(() -> permissionService.deleteById(targetId));

                verify(permissionRepository, times(1)).existsById(targetId);
                verify(permissionRepository, times(1)).deleteById(targetId);
                verifyNoInteractions(permissionMapper);
                verifyNoMoreInteractions(permissionRepository);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when attempting to delete non-existent ID")
            void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
                // Arrange
                Long nonExistentId = 404L;
                when(permissionRepository.existsById(nonExistentId)).thenReturn(false);

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> permissionService.deleteById(nonExistentId)
                );

                assertEquals("Permission with id '" + nonExistentId + "' does not exist.", exception.getMessage());
                verify(permissionRepository, times(1)).existsById(nonExistentId);
                verify(permissionRepository, never()).deleteById(anyLong());
                verifyNoInteractions(permissionMapper);
            }

            @Test
            @DisplayName("Should propagate DataIntegrityViolationException when deleting a permission in use by roles")
            void shouldThrowDataIntegrityViolationExceptionWhenForeignConstraintRestrictsDeletion() {
                // Arrange
                Long inUseId = 1L;
                when(permissionRepository.existsById(inUseId)).thenReturn(true);
                doThrow(new DataIntegrityViolationException("Foreign key violation: permission assigned to existing role"))
                        .when(permissionRepository).deleteById(inUseId);

                // Act & Assert
                assertThrows(DataIntegrityViolationException.class, () -> permissionService.deleteById(inUseId));

                verify(permissionRepository, times(1)).existsById(inUseId);
                verify(permissionRepository, times(1)).deleteById(inUseId);
                verifyNoInteractions(permissionMapper);
            }
        }
    }

    // =========================================================================
    // updateById(Long id, PermissionRequestDTO dto) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for updateById(Long id, PermissionRequestDTO dto)")
    class UpdateByIdTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should update fields and return updated DTO when ID exists")
            void shouldUpdateAndReturnDtoWhenIdExists() {
                // Arrange
                Long id = 1L;
                PermissionRequestDTO requestDTO = new PermissionRequestDTO("ADMIN_ACCESS");
                Permission existingPermission = new Permission(id, "USER_ACCESS");
                Permission updatedEntity = new Permission(id, "ADMIN_ACCESS");
                PermissionResponseDTO expectedResponse = new PermissionResponseDTO(id, "ADMIN_ACCESS");

                when(permissionRepository.findById(id)).thenReturn(Optional.of(existingPermission));
                doNothing().when(permissionMapper).updatePermissionFromDto(requestDTO, existingPermission);
                when(permissionRepository.save(existingPermission)).thenReturn(updatedEntity);
                when(permissionMapper.toPermissionResponseDTO(updatedEntity)).thenReturn(expectedResponse);

                // Act
                PermissionResponseDTO result = permissionService.updateById(id, requestDTO);

                // Assert
                assertAll("Verify updated fields",
                        () -> assertNotNull(result),
                        () -> assertEquals(id, result.getId()),
                        () -> assertEquals("ADMIN_ACCESS", result.getPermissionName())
                );

                verify(permissionRepository, times(1)).findById(id);
                verify(permissionMapper, times(1)).updatePermissionFromDto(requestDTO, existingPermission);
                verify(permissionRepository, times(1)).save(existingPermission);
                verify(permissionMapper, times(1)).toPermissionResponseDTO(updatedEntity);
                verifyNoMoreInteractions(permissionRepository, permissionMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
            void shouldThrowResourceNotFoundExceptionWhenIdNotFound() {
                // Arrange
                Long nonExistentId = 50L;
                PermissionRequestDTO requestDTO = new PermissionRequestDTO("NEW_NAME");
                when(permissionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> permissionService.updateById(nonExistentId, requestDTO)
                );

                assertEquals("Permission with id '" + nonExistentId + "' not found.", exception.getMessage());
                verify(permissionRepository, times(1)).findById(nonExistentId);
                verify(permissionMapper, never()).updatePermissionFromDto(any(), any());
                verify(permissionRepository, never()).save(any());
            }

            @Test
            @DisplayName("Should propagate DataIntegrityViolationException if updating collides with an existing permission name")
            void shouldThrowDataIntegrityViolationExceptionWhenUpdatedNameToUniqueCollision() {
                // Arrange
                Long id = 1L;
                PermissionRequestDTO requestDTO = new PermissionRequestDTO("COLLIDING_NAME");
                Permission existingPermission = new Permission(id, "OLD_NAME");

                when(permissionRepository.findById(id)).thenReturn(Optional.of(existingPermission));
                doNothing().when(permissionMapper).updatePermissionFromDto(requestDTO, existingPermission);
                when(permissionRepository.save(existingPermission))
                        .thenThrow(new DataIntegrityViolationException("Unique index violation"));

                // Act & Assert
                assertThrows(DataIntegrityViolationException.class, () -> permissionService.updateById(id, requestDTO));

                verify(permissionRepository, times(1)).findById(id);
                verify(permissionMapper, times(1)).updatePermissionFromDto(requestDTO, existingPermission);
                verify(permissionRepository, times(1)).save(existingPermission);
                verify(permissionMapper, never()).toPermissionResponseDTO(any());
            }
        }
    }
}