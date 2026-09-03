package com.auth.service.auth_service.service;

import com.auth.service.auth_service.dto.RoleRequestDTO;
import com.auth.service.auth_service.dto.RoleResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.mapper.IRoleMapper;
import com.auth.service.auth_service.model.Permission;
import com.auth.service.auth_service.model.Role;
import com.auth.service.auth_service.repository.IPermissionRepository;
import com.auth.service.auth_service.repository.IRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Unit Tests")
class RoleServiceTest {

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IPermissionRepository permissionRepository;

    @Mock
    private IRoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    private RoleRequestDTO testRoleRequestDTO;
    private RoleResponseDTO testRoleResponseDTO;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = new Permission();
        testPermission.setId(1L);
        testPermission.setPermissionName("READ_PRIVILEGE");

        Set<Permission> permissions = new HashSet<>();
        permissions.add(testPermission);

        testRole = new Role();
        testRole.setId(1L);
        testRole.setRole("ROLE_USER");
        testRole.setPermissionSet(permissions);

        testRoleRequestDTO = new RoleRequestDTO();
        testRoleRequestDTO.setRole("ROLE_USER");

        testRoleResponseDTO = new RoleResponseDTO();
        testRoleResponseDTO.setId(1L);
        testRoleResponseDTO.setRole("ROLE_USER");
    }

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
            @DisplayName("Should return list of RoleResponseDTO when roles exist")
            void shouldReturnRoleListWhenRolesExist() {
                // Arrange
                List<Role> roleEntities = List.of(testRole);
                List<RoleResponseDTO> expectedResponse = List.of(testRoleResponseDTO);

                when(roleRepository.findAll()).thenReturn(roleEntities);
                when(roleMapper.toRoleResponseDTOList(roleEntities)).thenReturn(expectedResponse);

                // Act
                List<RoleResponseDTO> actualResponse = roleService.findAll();

                // Assert
                assertAll("Verify returned role list",
                        () -> assertNotNull(actualResponse, "The response list must not be null"),
                        () -> assertEquals(1, actualResponse.size(), "List must contain exactly 1 role"),
                        () -> assertEquals(1L, actualResponse.get(0).getId()),
                        () -> assertEquals("ROLE_USER", actualResponse.get(0).getRole())
                );

                verify(roleRepository, times(1)).findAll();
                verify(roleMapper, times(1)).toRoleResponseDTOList(roleEntities);
                verifyNoMoreInteractions(roleRepository, roleMapper);
            }

            @Test
            @DisplayName("Should return empty list when no roles exist in database")
            void shouldReturnEmptyListWhenDatabaseIsEmpty() {
                // Arrange
                when(roleRepository.findAll()).thenReturn(Collections.emptyList());
                when(roleMapper.toRoleResponseDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

                // Act
                List<RoleResponseDTO> actualResponse = roleService.findAll();

                // Assert
                assertAll("Verify empty list response",
                        () -> assertNotNull(actualResponse, "Response should not be null"),
                        () -> assertTrue(actualResponse.isEmpty(), "Response list should be empty")
                );

                verify(roleRepository, times(1)).findAll();
                verify(roleMapper, times(1)).toRoleResponseDTOList(Collections.emptyList());
                verifyNoMoreInteractions(roleRepository, roleMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should propagate unexpected RuntimeException when repository fails")
            void shouldPropagateExceptionWhenRepositoryFails() {
                // Arrange
                when(roleRepository.findAll()).thenThrow(new RuntimeException("Database unreachable"));

                // Act & Assert
                RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.findAll());
                assertEquals("Database unreachable", ex.getMessage());

                verify(roleRepository, times(1)).findAll();
                verifyNoInteractions(roleMapper);
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
            @DisplayName("Should return RoleResponseDTO when ID exists")
            void shouldReturnRoleDtoWhenIdExists() {
                // Arrange
                Long targetId = 1L;
                when(roleRepository.findById(targetId)).thenReturn(Optional.of(testRole));
                when(roleMapper.toRoleResponseDTO(testRole)).thenReturn(testRoleResponseDTO);

                // Act
                RoleResponseDTO actualResult = roleService.findById(targetId);

                // Assert
                assertAll("Verify found role details",
                        () -> assertNotNull(actualResult, "Result must not be null"),
                        () -> assertEquals(targetId, actualResult.getId()),
                        () -> assertEquals("ROLE_USER", actualResult.getRole())
                );

                verify(roleRepository, times(1)).findById(targetId);
                verify(roleMapper, times(1)).toRoleResponseDTO(testRole);
                verifyNoMoreInteractions(roleRepository, roleMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when role ID does not exist")
            void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
                // Arrange
                Long nonExistentId = 999L;
                when(roleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> roleService.findById(nonExistentId)
                );

                assertEquals("Role not found.", exception.getMessage());
                verify(roleRepository, times(1)).findById(nonExistentId);
                verifyNoInteractions(roleMapper);
            }
        }
    }

    // =========================================================================
    // save(RoleRequestDTO dto) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for save(RoleRequestDTO dto)")
    class SaveTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should save role and attach existing permissions")
            void shouldSaveAndReturnRoleDtoWhenRequestIsValid() {
                // Arrange
                when(roleMapper.toRole(testRoleRequestDTO)).thenReturn(testRole);
                when(permissionRepository.findById(1L)).thenReturn(Optional.of(testPermission));
                when(roleRepository.save(any(Role.class))).thenReturn(testRole);
                when(roleMapper.toRoleResponseDTO(testRole)).thenReturn(testRoleResponseDTO);

                // Act
                RoleResponseDTO actualResult = roleService.save(testRoleRequestDTO);

                // Assert
                assertAll("Verify saved role response",
                        () -> assertNotNull(actualResult),
                        () -> assertEquals(1L, actualResult.getId()),
                        () -> assertEquals("ROLE_USER", actualResult.getRole())
                );

                verify(roleMapper, times(1)).toRole(testRoleRequestDTO);
                verify(permissionRepository, times(1)).findById(1L);
                verify(roleRepository, times(1)).save(testRole);
                verify(roleMapper, times(1)).toRoleResponseDTO(testRole);
                verifyNoMoreInteractions(roleRepository, permissionRepository, roleMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should propagate DataIntegrityViolationException when role name already exists")
            void shouldThrowDataIntegrityViolationExceptionWhenRoleNameIsDuplicate() {
                // Arrange
                when(roleMapper.toRole(testRoleRequestDTO)).thenReturn(testRole);
                when(permissionRepository.findById(1L)).thenReturn(Optional.of(testPermission));
                when(roleRepository.save(any(Role.class)))
                        .thenThrow(new DataIntegrityViolationException("Unique constraint violation: duplicate role name"));

                // Act & Assert
                assertThrows(DataIntegrityViolationException.class, () -> roleService.save(testRoleRequestDTO));

                verify(roleMapper, times(1)).toRole(testRoleRequestDTO);
                verify(permissionRepository, times(1)).findById(1L);
                verify(roleRepository, times(1)).save(testRole);
                verify(roleMapper, never()).toRoleResponseDTO(any());
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
            @DisplayName("Should delete role successfully when ID exists")
            void shouldDeleteRoleWhenIdExists() {
                // Arrange
                Long targetId = 1L;
                when(roleRepository.existsById(targetId)).thenReturn(true);
                doNothing().when(roleRepository).deleteById(targetId);

                // Act & Assert
                assertDoesNotThrow(() -> roleService.deleteById(targetId));

                verify(roleRepository, times(1)).existsById(targetId);
                verify(roleRepository, times(1)).deleteById(targetId);
                verifyNoMoreInteractions(roleRepository);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when role ID does not exist")
            void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
                // Arrange
                Long nonExistentId = 999L;
                when(roleRepository.existsById(nonExistentId)).thenReturn(false);

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> roleService.deleteById(nonExistentId)
                );

                assertEquals("Id not found.", exception.getMessage());
                verify(roleRepository, times(1)).existsById(nonExistentId);
                verify(roleRepository, never()).deleteById(anyLong());
                verifyNoMoreInteractions(roleRepository);
            }

            @Test
            @DisplayName("Should propagate DataIntegrityViolationException when deleting a role assigned to users")
            void shouldThrowDataIntegrityViolationExceptionWhenRoleIsAssignedToUsers() {
                // Arrange
                Long inUseId = 1L;
                when(roleRepository.existsById(inUseId)).thenReturn(true);
                doThrow(new DataIntegrityViolationException("Foreign key violation: role is currently assigned to active users"))
                        .when(roleRepository).deleteById(inUseId);

                // Act & Assert
                assertThrows(DataIntegrityViolationException.class, () -> roleService.deleteById(inUseId));

                verify(roleRepository, times(1)).existsById(inUseId);
                verify(roleRepository, times(1)).deleteById(inUseId);
                verifyNoMoreInteractions(roleRepository);
            }
        }
    }

    // =========================================================================
    // updateById(Long id, RoleRequestDTO dto) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for updateById(Long id, RoleRequestDTO dto)")
    class UpdateByIdTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should update and return RoleResponseDTO when ID exists")
            void shouldUpdateAndReturnRoleDtoWhenIdExists() {
                // Arrange
                Long targetId = 1L;
                when(roleRepository.findById(targetId)).thenReturn(Optional.of(testRole));
                doNothing().when(roleMapper).updateRoleFromDTO(testRoleRequestDTO, testRole);
                when(permissionRepository.findById(1L)).thenReturn(Optional.of(testPermission));
                when(roleRepository.save(testRole)).thenReturn(testRole);
                when(roleMapper.toRoleResponseDTO(testRole)).thenReturn(testRoleResponseDTO);

                // Act
                RoleResponseDTO actualResult = roleService.updateById(targetId, testRoleRequestDTO);

                // Assert
                assertAll("Verify updated role details",
                        () -> assertNotNull(actualResult),
                        () -> assertEquals(targetId, actualResult.getId()),
                        () -> assertEquals("ROLE_USER", actualResult.getRole())
                );

                verify(roleRepository, times(1)).findById(targetId);
                verify(roleMapper, times(1)).updateRoleFromDTO(testRoleRequestDTO, testRole);
                verify(permissionRepository, times(1)).findById(1L);
                verify(roleRepository, times(1)).save(testRole);
                verify(roleMapper, times(1)).toRoleResponseDTO(testRole);
                verifyNoMoreInteractions(roleRepository, permissionRepository, roleMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when updating non-existent role ID")
            void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
                // Arrange
                Long nonExistentId = 999L;
                when(roleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> roleService.updateById(nonExistentId, testRoleRequestDTO)
                );

                assertEquals("Role not found.", exception.getMessage());
                verify(roleRepository, times(1)).findById(nonExistentId);
                verifyNoInteractions(roleMapper, permissionRepository);
                verify(roleRepository, never()).save(any());
            }

            @Test
            @DisplayName("Should propagate DataIntegrityViolationException when update collides with existing role name")
            void shouldThrowDataIntegrityViolationExceptionWhenUpdatedNameCollides() {
                // Arrange
                Long targetId = 1L;
                when(roleRepository.findById(targetId)).thenReturn(Optional.of(testRole));
                doNothing().when(roleMapper).updateRoleFromDTO(testRoleRequestDTO, testRole);
                when(permissionRepository.findById(1L)).thenReturn(Optional.of(testPermission));
                when(roleRepository.save(testRole))
                        .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

                // Act & Assert
                assertThrows(DataIntegrityViolationException.class, () -> roleService.updateById(targetId, testRoleRequestDTO));

                verify(roleRepository, times(1)).findById(targetId);
                verify(roleMapper, times(1)).updateRoleFromDTO(testRoleRequestDTO, testRole);
                verify(permissionRepository, times(1)).findById(1L);
                verify(roleRepository, times(1)).save(testRole);
                verify(roleMapper, never()).toRoleResponseDTO(any());
            }
        }
    }

    // =========================================================================
    // validatePermissionsExists(Role role) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for validatePermissionsExists(Role role)")
    class ValidatePermissionsExistsTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should filter and keep only permissions that exist in the database")
            void shouldFilterOutNonExistentPermissions() {
                // Arrange
                Permission existingPermission = new Permission();
                existingPermission.setId(1L);
                existingPermission.setPermissionName("READ_PRIVILEGE");

                Permission nonExistentPermission = new Permission();
                nonExistentPermission.setId(2L);
                nonExistentPermission.setPermissionName("UNKNOWN_PRIVILEGE");

                Role roleWithMultiplePermissions = new Role();
                roleWithMultiplePermissions.setPermissionSet(new HashSet<>(Set.of(existingPermission, nonExistentPermission)));

                when(permissionRepository.findById(1L)).thenReturn(Optional.of(existingPermission));
                when(permissionRepository.findById(2L)).thenReturn(Optional.empty());

                // Act
                Role validatedRole = roleService.validatePermissionsExists(roleWithMultiplePermissions);

                // Assert
                assertAll("Verify permission filtering",
                        () -> assertNotNull(validatedRole.getPermissionSet()),
                        () -> assertEquals(1, validatedRole.getPermissionSet().size(), "Only existing permission should remain"),
                        () -> assertTrue(validatedRole.getPermissionSet().contains(existingPermission)),
                        () -> assertFalse(validatedRole.getPermissionSet().contains(nonExistentPermission))
                );

                verify(permissionRepository, times(1)).findById(1L);
                verify(permissionRepository, times(1)).findById(2L);
                verifyNoMoreInteractions(permissionRepository);
            }

            @Test
            @DisplayName("Should retain all permissions when all of them exist in the database")
            void shouldKeepAllPermissionsWhenAllExist() {
                // Arrange
                Permission perm1 = new Permission();
                perm1.setId(1L);
                perm1.setPermissionName("READ_PRIVILEGE");

                Permission perm2 = new Permission();
                perm2.setId(2L);
                perm2.setPermissionName("WRITE_PRIVILEGE");

                Role roleWithAllValidPermissions = new Role();
                roleWithAllValidPermissions.setPermissionSet(new HashSet<>(Set.of(perm1, perm2)));

                when(permissionRepository.findById(1L)).thenReturn(Optional.of(perm1));
                when(permissionRepository.findById(2L)).thenReturn(Optional.of(perm2));

                // Act
                Role validatedRole = roleService.validatePermissionsExists(roleWithAllValidPermissions);

                // Assert
                assertAll("Verify all permissions retained",
                        () -> assertEquals(2, validatedRole.getPermissionSet().size()),
                        () -> assertTrue(validatedRole.getPermissionSet().contains(perm1)),
                        () -> assertTrue(validatedRole.getPermissionSet().contains(perm2))
                );

                verify(permissionRepository, times(1)).findById(1L);
                verify(permissionRepository, times(1)).findById(2L);
                verifyNoMoreInteractions(permissionRepository);
            }

            @Test
            @DisplayName("Should result in empty set when none of the permissions exist in the database")
            void shouldReturnEmptySetWhenNoPermissionsExist() {
                // Arrange
                Permission perm1 = new Permission();
                perm1.setId(10L);
                perm1.setPermissionName("NON_EXISTENT_1");

                Role roleWithInvalidPermissions = new Role();
                roleWithInvalidPermissions.setPermissionSet(new HashSet<>(Set.of(perm1)));

                when(permissionRepository.findById(10L)).thenReturn(Optional.empty());

                // Act
                Role validatedRole = roleService.validatePermissionsExists(roleWithInvalidPermissions);

                // Assert
                assertTrue(validatedRole.getPermissionSet().isEmpty(), "Permission set must be empty");
                verify(permissionRepository, times(1)).findById(10L);
                verifyNoMoreInteractions(permissionRepository);
            }
        }
    }
}