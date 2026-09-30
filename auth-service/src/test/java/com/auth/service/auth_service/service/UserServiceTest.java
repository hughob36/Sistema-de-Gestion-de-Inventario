package com.auth.service.auth_service.service;

import com.auth.service.auth_service.dto.UserAppRequestDTO;
import com.auth.service.auth_service.dto.UserAppResponseDTO;
import com.auth.service.auth_service.exception.ResourceNotFoundException;
import com.auth.service.auth_service.mapper.IUserMapper;
import com.auth.service.auth_service.model.Role;
import com.auth.service.auth_service.model.UserApp;
import com.auth.service.auth_service.repository.IRoleRepository;
import com.auth.service.auth_service.repository.IUserAppRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private IUserAppRepository userAppRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IUserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserApp testUserApp;
    private UserAppRequestDTO testUserAppRequestDTO;
    private UserAppResponseDTO testUserAppResponseDTO;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setRole("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUserApp = new UserApp();
        testUserApp.setId(1L);
        testUserApp.setName("Hugo");
        testUserApp.setLastname("Benítez");
        testUserApp.setDni("12345678");
        testUserApp.setUsername("hugo.benitez");
        testUserApp.setPassword("encodedPassword123");
        testUserApp.setEnable(true);
        testUserApp.setAccountNotExpired(true);
        testUserApp.setAccountNotLocked(true);
        testUserApp.setCredentialNotExpired(true);
        testUserApp.setRoleSet(roles);

        testUserAppRequestDTO = new UserAppRequestDTO();
        testUserAppRequestDTO.setName("Hugo");
        testUserAppRequestDTO.setLastname("Benítez");
        testUserAppRequestDTO.setDni("12345678");
        testUserAppRequestDTO.setUsername("hugo.benitez");
        testUserAppRequestDTO.setPassword("rawPassword123");
        testUserAppRequestDTO.setEnable(true);
        testUserAppRequestDTO.setAccountNotExpired(true);
        testUserAppRequestDTO.setAccountNotLocked(true);
        testUserAppRequestDTO.setCredentialNotExpired(true);
        testUserAppRequestDTO.setRoleSet(roles);

        testUserAppResponseDTO = new UserAppResponseDTO();
        testUserAppResponseDTO.setId(1L);
        testUserAppResponseDTO.setName("Hugo");
        testUserAppResponseDTO.setLastname("Benítez");
        testUserAppResponseDTO.setDni("12345678");
        testUserAppResponseDTO.setUsername("hugo.benitez");
        testUserAppResponseDTO.setPassword("encodedPassword123");
        testUserAppResponseDTO.setEnable(true);
        testUserAppResponseDTO.setAccountNotExpired(true);
        testUserAppResponseDTO.setAccountNotLocked(true);
        testUserAppResponseDTO.setCredentialNotExpired(true);
        testUserAppResponseDTO.setRoleSet(roles);
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
            @DisplayName("Should return list of UserAppResponseDTO when users exist")
            void shouldReturnUserAppListWhenUsersExist() {
                // Arrange
                List<UserApp> userEntities = List.of(testUserApp);
                List<UserAppResponseDTO> expectedResponse = List.of(testUserAppResponseDTO);

                when(userAppRepository.findAll()).thenReturn(userEntities);
                when(userMapper.toUserAppResponseDTOList(userEntities)).thenReturn(expectedResponse);

                // Act
                List<UserAppResponseDTO> actualResponse = userService.findAll();

                // Assert
                assertAll("Verify returned user list",
                        () -> assertNotNull(actualResponse, "The response list must not be null"),
                        () -> assertEquals(1, actualResponse.size(), "List must contain exactly 1 user"),
                        () -> assertEquals(1L, actualResponse.get(0).getId()),
                        () -> assertEquals("hugo.benitez", actualResponse.get(0).getUsername())
                );

                verify(userAppRepository, times(1)).findAll();
                verify(userMapper, times(1)).toUserAppResponseDTOList(userEntities);
                verifyNoMoreInteractions(userAppRepository, userMapper);
            }

            @Test
            @DisplayName("Should return empty list when no users exist in database")
            void shouldReturnEmptyListWhenDatabaseIsEmpty() {
                // Arrange
                when(userAppRepository.findAll()).thenReturn(Collections.emptyList());
                when(userMapper.toUserAppResponseDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

                // Act
                List<UserAppResponseDTO> actualResponse = userService.findAll();

                // Assert
                assertAll("Verify empty list response",
                        () -> assertNotNull(actualResponse, "Response should not be null"),
                        () -> assertTrue(actualResponse.isEmpty(), "Response list should be empty")
                );

                verify(userAppRepository, times(1)).findAll();
                verify(userMapper, times(1)).toUserAppResponseDTOList(Collections.emptyList());
                verifyNoMoreInteractions(userAppRepository, userMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should propagate unexpected RuntimeException when repository fails")
            void shouldPropagateExceptionWhenRepositoryFails() {
                // Arrange
                when(userAppRepository.findAll()).thenThrow(new RuntimeException("Database unreachable"));

                // Act & Assert
                RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findAll());
                assertEquals("Database unreachable", ex.getMessage());

                verify(userAppRepository, times(1)).findAll();
                verifyNoInteractions(userMapper);
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
            @DisplayName("Should return UserAppResponseDTO when ID exists")
            void shouldReturnUserDtoWhenIdExists() {
                // Arrange
                Long targetId = 1L;
                when(userAppRepository.findById(targetId)).thenReturn(Optional.of(testUserApp));
                when(userMapper.toUserAppResponseDTO(testUserApp)).thenReturn(testUserAppResponseDTO);

                // Act
                UserAppResponseDTO actualResult = userService.findById(targetId);

                // Assert
                assertAll("Verify found user details",
                        () -> assertNotNull(actualResult, "Result must not be null"),
                        () -> assertEquals(targetId, actualResult.getId()),
                        () -> assertEquals("hugo.benitez", actualResult.getUsername())
                );

                verify(userAppRepository, times(1)).findById(targetId);
                verify(userMapper, times(1)).toUserAppResponseDTO(testUserApp);
                verifyNoMoreInteractions(userAppRepository, userMapper);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when user ID does not exist")
            void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
                // Arrange
                Long nonExistentId = 999L;
                when(userAppRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.findById(nonExistentId)
                );

                assertEquals("User not found.", exception.getMessage());
                verify(userAppRepository, times(1)).findById(nonExistentId);
                verifyNoInteractions(userMapper);
            }
        }
    }

    // =========================================================================
    // save(UserAppRequestDTO dto) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for save(UserAppRequestDTO dto)")
    class SaveTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should encode password, validate roles, save user and return UserAppResponseDTO")
            void shouldSaveAndReturnUserDtoWhenRequestIsValid() {
                // Arrange
                when(userMapper.toUserApp(testUserAppRequestDTO)).thenReturn(testUserApp);
                when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
                when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword123");
                when(userAppRepository.save(any(UserApp.class))).thenReturn(testUserApp);
                when(userMapper.toUserAppResponseDTO(testUserApp)).thenReturn(testUserAppResponseDTO);

                // Act
                UserAppResponseDTO actualResult = userService.save(testUserAppRequestDTO);

                // Assert
                assertAll("Verify saved user response",
                        () -> assertNotNull(actualResult),
                        () -> assertEquals(1L, actualResult.getId()),
                        () -> assertEquals("hugo.benitez", actualResult.getUsername())
                );

                verify(userMapper, times(1)).toUserApp(testUserAppRequestDTO);
                verify(roleRepository, times(1)).findById(1L);
                verify(passwordEncoder, times(1)).encode(anyString());
                verify(userAppRepository, times(1)).save(testUserApp);
                verify(userMapper, times(1)).toUserAppResponseDTO(testUserApp);
                verifyNoMoreInteractions(userAppRepository, roleRepository, userMapper, passwordEncoder);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should propagate DataIntegrityViolationException when username or DNI already exists")
            void shouldThrowDataIntegrityViolationExceptionWhenDuplicate() {
                // Arrange
                when(userMapper.toUserApp(testUserAppRequestDTO)).thenReturn(testUserApp);
                when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
                when(userAppRepository.save(any(UserApp.class)))
                        .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

                // Act & Assert
                assertThrows(DataIntegrityViolationException.class, () -> userService.save(testUserAppRequestDTO));

                verify(userMapper, times(1)).toUserApp(testUserAppRequestDTO);
                verify(roleRepository, times(1)).findById(1L);
                verify(passwordEncoder, times(1)).encode(anyString());
                verify(userAppRepository, times(1)).save(testUserApp);
                verify(userMapper, never()).toUserAppResponseDTO(any());
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
            @DisplayName("Should delete user successfully when ID exists")
            void shouldDeleteUserWhenIdExists() {
                // Arrange
                Long targetId = 1L;
                when(userAppRepository.existsById(targetId)).thenReturn(true);
                doNothing().when(userAppRepository).deleteById(targetId);

                // Act & Assert
                assertDoesNotThrow(() -> userService.deleteById(targetId));

                verify(userAppRepository, times(1)).existsById(targetId);
                verify(userAppRepository, times(1)).deleteById(targetId);
                verifyNoMoreInteractions(userAppRepository);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when user ID does not exist")
            void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
                // Arrange
                Long nonExistentId = 999L;
                when(userAppRepository.existsById(nonExistentId)).thenReturn(false);

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.deleteById(nonExistentId)
                );

                assertEquals("Id not exists.", exception.getMessage());
                verify(userAppRepository, times(1)).existsById(nonExistentId);
                verify(userAppRepository, never()).deleteById(anyLong());
                verifyNoMoreInteractions(userAppRepository);
            }
        }
    }

    // =========================================================================
    // updateById(Long id, UserAppRequestDTO dto) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for updateById(Long id, UserAppRequestDTO dto)")
    class UpdateByIdTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should update user, encode password and return UserAppResponseDTO when ID exists")
            void shouldUpdateAndReturnUserDtoWhenIdExists() {
                // Arrange
                Long targetId = 1L;
                testUserAppRequestDTO.setPassword("newRawPassword123");

                when(userAppRepository.findById(targetId)).thenReturn(Optional.of(testUserApp));
                doNothing().when(userMapper).updateUserFromDTO(testUserAppRequestDTO, testUserApp);
                when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
                when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword123");
                when(userAppRepository.save(testUserApp)).thenReturn(testUserApp);
                when(userMapper.toUserAppResponseDTO(testUserApp)).thenReturn(testUserAppResponseDTO);

                // Act
                UserAppResponseDTO actualResult = userService.updateById(targetId, testUserAppRequestDTO);

                // Assert
                assertAll("Verify updated user details",
                        () -> assertNotNull(actualResult),
                        () -> assertEquals(targetId, actualResult.getId())
                );

                verify(userAppRepository, times(1)).findById(targetId);
                verify(userMapper, times(1)).updateUserFromDTO(testUserAppRequestDTO, testUserApp);
                verify(roleRepository, times(1)).findById(1L);
                verify(passwordEncoder, times(1)).encode(anyString());
                verify(userAppRepository, times(1)).save(testUserApp);
                verify(userMapper, times(1)).toUserAppResponseDTO(testUserApp);
                verifyNoMoreInteractions(userAppRepository, roleRepository, userMapper, passwordEncoder);
            }
        }

        @Nested
        @DisplayName("Negative / Exceptional Scenarios")
        class NegativeScenarios {

            @Test
            @DisplayName("Should throw ResourceNotFoundException when updating non-existent user ID")
            void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
                // Arrange
                Long nonExistentId = 999L;
                when(userAppRepository.findById(nonExistentId)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.updateById(nonExistentId, testUserAppRequestDTO)
                );

                assertEquals("User not found.", exception.getMessage());
                verify(userAppRepository, times(1)).findById(nonExistentId);
                verifyNoInteractions(userMapper, roleRepository, passwordEncoder);
                verify(userAppRepository, never()).save(any());
            }
        }
    }

    // =========================================================================
    // validateRoleExists(UserApp userApp) Tests
    // =========================================================================
    @Nested
    @DisplayName("Tests for validateRoleExists(UserApp userApp)")
    class ValidateRoleExistsTests {

        @Nested
        @DisplayName("Positive Scenarios")
        class PositiveScenarios {

            @Test
            @DisplayName("Should filter and keep only roles that exist in the database")
            void shouldFilterOutNonExistentRoles() {
                // Arrange
                Role existingRole = new Role();
                existingRole.setId(1L);
                existingRole.setRole("ROLE_USER");

                Role nonExistentRole = new Role();
                nonExistentRole.setId(2L);
                nonExistentRole.setRole("ROLE_UNKNOWN");

                UserApp userWithMultipleRoles = new UserApp();
                userWithMultipleRoles.setRoleSet(new HashSet<>(Set.of(existingRole, nonExistentRole)));

                when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
                when(roleRepository.findById(2L)).thenReturn(Optional.empty());

                // Act
                UserApp validatedUser = userService.validateRoleExists(userWithMultipleRoles);

                // Assert
                assertAll("Verify role filtering",
                        () -> assertNotNull(validatedUser.getRoleSet()),
                        () -> assertEquals(1, validatedUser.getRoleSet().size(), "Only existing role should remain"),
                        () -> assertTrue(validatedUser.getRoleSet().contains(existingRole)),
                        () -> assertFalse(validatedUser.getRoleSet().contains(nonExistentRole))
                );

                verify(roleRepository, times(1)).findById(1L);
                verify(roleRepository, times(1)).findById(2L);
                verifyNoMoreInteractions(roleRepository);
            }

            @Test
            @DisplayName("Should result in empty role set when none of the roles exist in the database")
            void shouldReturnEmptySetWhenNoRolesExist() {
                // Arrange
                Role role1 = new Role();
                role1.setId(10L);
                role1.setRole("NON_EXISTENT");

                UserApp userWithInvalidRoles = new UserApp();
                userWithInvalidRoles.setRoleSet(new HashSet<>(Set.of(role1)));

                when(roleRepository.findById(10L)).thenReturn(Optional.empty());

                // Act
                UserApp validatedUser = userService.validateRoleExists(userWithInvalidRoles);

                // Assert
                assertTrue(validatedUser.getRoleSet().isEmpty(), "Role set must be empty");
                verify(roleRepository, times(1)).findById(10L);
                verifyNoMoreInteractions(roleRepository);
            }
        }
    }
}
