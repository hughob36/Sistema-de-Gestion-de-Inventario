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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IPermissionRepository permissionRepository;

    @Mock
    private IRoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleRequestDTO roleRequestDTO;
    private RoleResponseDTO roleResponseDTO;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = new Permission();
        permission.setId(1L);
        permission.setPermissionName("READ_PRIVILEGES");

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        role = new Role();
        role.setId(1L);
        role.setRole("ROLE_USER");
        role.setPermissionSet(permissions);

        roleRequestDTO = new RoleRequestDTO();
        roleResponseDTO = new RoleResponseDTO();
    }

    @Nested
    @DisplayName("Pruebas para findAll")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar la lista de roles exitosamente")
        void findAll_Success() {
            List<Role> roles = List.of(role);
            List<RoleResponseDTO> expectedResponse = List.of(roleResponseDTO);

            when(roleRepository.findAll()).thenReturn(roles);
            when(roleMapper.toRoleResponseDTOList(roles)).thenReturn(expectedResponse);

            List<RoleResponseDTO> actualResponse = roleService.findAll();

            assertNotNull(actualResponse);
            assertEquals(1, actualResponse.size());
            verify(roleRepository, times(1)).findAll();
            verify(roleMapper, times(1)).toRoleResponseDTOList(roles);
        }
    }

    @Nested
    @DisplayName("Pruebas para findById")
    class FindByIdTests {

        @Test
        @DisplayName("Caso Positivo: Debe retornar el rol cuando existe el ID")
        void findById_Success() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            when(roleMapper.toRoleResponseDTO(role)).thenReturn(roleResponseDTO);

            RoleResponseDTO result = roleService.findById(1L);

            assertNotNull(result);
            verify(roleRepository, times(1)).findById(1L);
            verify(roleMapper, times(1)).toRoleResponseDTO(role);
        }

        @Test
        @DisplayName("Caso Negativo: Debe lanzar ResourceNotFoundException cuando no existe el ID")
        void findById_NotFound_ThrowsException() {
            when(roleRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> roleService.findById(1L)
            );

            assertEquals("Role not found.", exception.getMessage());
            verify(roleRepository, times(1)).findById(1L);
            verifyNoInteractions(roleMapper);
        }
    }

    @Nested
    @DisplayName("Pruebas para save")
    class SaveTests {

        @Test
        @DisplayName("Caso Positivo: Debe guardar el rol y validar sus permisos")
        void save_Success() {
            when(roleMapper.toRole(roleRequestDTO)).thenReturn(role);
            when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
            when(roleRepository.save(any(Role.class))).thenReturn(role);
            when(roleMapper.toRoleResponseDTO(role)).thenReturn(roleResponseDTO);

            RoleResponseDTO result = roleService.save(roleRequestDTO);

            assertNotNull(result);
            verify(roleMapper, times(1)).toRole(roleRequestDTO);
            verify(permissionRepository, times(1)).findById(1L);
            verify(roleRepository, times(1)).save(role);
            verify(roleMapper, times(1)).toRoleResponseDTO(role);
        }
    }

    @Nested
    @DisplayName("Pruebas para deleteById")
    class DeleteByIdTests {

        @Test
        @DisplayName("Caso Positivo: Debe eliminar el rol si existe")
        void deleteById_Success() {
            when(roleRepository.existsById(1L)).thenReturn(true);

            assertDoesNotThrow(() -> roleService.deleteById(1L));

            verify(roleRepository, times(1)).existsById(1L);
            verify(roleRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Caso Negativo: Debe lanzar ResourceNotFoundException si el ID no existe")
        void deleteById_NotFound_ThrowsException() {
            when(roleRepository.existsById(1L)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> roleService.deleteById(1L)
            );

            assertEquals("Id not found.", exception.getMessage());
            verify(roleRepository, times(1)).existsById(1L);
            verify(roleRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Pruebas para updateById")
    class UpdateByIdTests {

        @Test
        @DisplayName("Caso Positivo: Debe actualizar el rol cuando el ID existe")
        void updateById_Success() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
            doNothing().when(roleMapper).updateRoleFromDTO(roleRequestDTO, role);
            when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
            when(roleRepository.save(role)).thenReturn(role);
            when(roleMapper.toRoleResponseDTO(role)).thenReturn(roleResponseDTO);

            RoleResponseDTO result = roleService.updateById(1L, roleRequestDTO);

            assertNotNull(result);
            verify(roleRepository, times(1)).findById(1L);
            verify(roleMapper, times(1)).updateRoleFromDTO(roleRequestDTO, role);
            verify(permissionRepository, times(1)).findById(1L);
            verify(roleRepository, times(1)).save(role);
            verify(roleMapper, times(1)).toRoleResponseDTO(role);
        }

        @Test
        @DisplayName("Caso Negativo: Debe lanzar ResourceNotFoundException si el ID no existe")
        void updateById_NotFound_ThrowsException() {
            when(roleRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> roleService.updateById(1L, roleRequestDTO)
            );

            assertEquals("Role not found.", exception.getMessage());
            verify(roleRepository, times(1)).findById(1L);
            verifyNoInteractions(permissionRepository);
            verify(roleRepository, never()).save(any(Role.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para validatePermissionsExists")
    class ValidatePermissionsExistsTests {

        @Test
        @DisplayName("Debe filtrar y mantener solo los permisos que existen en base de datos")
        void validatePermissionsExists_FiltersNonExistentPermissions() {
            Permission perm1 = new Permission();
            perm1.setId(1L);

            Permission perm2 = new Permission();
            perm2.setId(2L);

            Role roleWithMultiplePermissions = new Role();
            roleWithMultiplePermissions.setPermissionSet(Set.of(perm1, perm2));

            // Solo existe el permiso 1
            when(permissionRepository.findById(1L)).thenReturn(Optional.of(perm1));
            when(permissionRepository.findById(2L)).thenReturn(Optional.empty());

            Role validatedRole = roleService.validatePermissionsExists(roleWithMultiplePermissions);

            assertEquals(1, validatedRole.getPermissionSet().size());
            assertTrue(validatedRole.getPermissionSet().contains(perm1));
            assertFalse(validatedRole.getPermissionSet().contains(perm2));
        }
    }
}