package com.ecommerce.userapi.service;

import com.ecommerce.userapi.domain.dto.UserRequestDto;
import com.ecommerce.userapi.domain.dto.UserResponseDto;
import com.ecommerce.userapi.domain.entity.User;
import com.ecommerce.userapi.exception.UserAlreadyExistsException;
import com.ecommerce.userapi.exception.UserNotFoundException;
import com.ecommerce.userapi.mapper.impl.MapperImpl;
import com.ecommerce.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MapperImpl mapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDto userResponseDto;
    private PageRequest pageRequest;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        // Configurando dados de teste
        user = User.builder()
                .id(1L)
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .dataRegister(LocalDateTime.now())
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .dataRegister(user.getDataRegister())
                .build();

        pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "name");

        userRequestDto = UserRequestDto.builder()
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .build();
    }

    @Test
    @DisplayName("findAll deve retornar lista de UserResponseDto quando sucesso")
    void findAll_ShouldReturnListOfUserResponseDto_WhenSuccessful() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(mapper.mapTo(user)).thenReturn(userResponseDto);

        // Act
        List<UserResponseDto> result = userService.findAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(result.get(0))
                .usingRecursiveComparison()
                .isEqualTo(userResponseDto);

        verify(userRepository, times(1)).findAll();
        verify(mapper, times(1)).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando nenhum usuário for encontrado")
    void findAll_ShouldReturnEmptyList_WhenNoUsersFound() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserResponseDto> result = userService.findAll();

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(userRepository, times(1)).findAll();
        verify(mapper, never()).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findByPage deve retornar Page de UserResponseDto quando sucesso")
    void findByPage_ShouldReturnPageOfUserResponseDto_WhenSuccessful() {
        // Arrange
        Page<User> userPage = new PageImpl<>(List.of(user), pageRequest, 1);
        when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        when(mapper.mapTo(user)).thenReturn(userResponseDto);

        // Act
        Page<UserResponseDto> result = userService.findByPage(pageRequest);

        // Assert
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(result.getContent().get(0))
                .usingRecursiveComparison()
                .isEqualTo(userResponseDto);

        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository, times(1)).findAll(pageRequest);
        verify(mapper, times(1)).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findByPage deve retornar Page vazio quando nenhum usuário for encontrado")
    void findByPage_ShouldReturnEmptyPage_WhenNoUsersFound() {
        // Arrange
        Page<User> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);
        when(userRepository.findAll(pageRequest)).thenReturn(emptyPage);

        // Act
        Page<UserResponseDto> result = userService.findByPage(pageRequest);

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();

        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isZero();

        verify(userRepository, times(1)).findAll(pageRequest);
        verify(mapper, never()).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findById deve retornar UserResponseDto quando usuário existe")
    void findById_ShouldReturnUserResponseDto_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.mapTo(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.findById(userId);

        // Assert
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userResponseDto);

        verify(userRepository, times(1)).findById(userId);
        verify(mapper, times(1)).mapTo(user);
    }

    @Test
    @DisplayName("findById deve lançar UserNotFoundException quando usuário não existe")
    void findById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(mapper, never()).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findByCpf deve retornar UserResponseDto quando usuário existe")
    void findByCpf_ShouldReturnUserResponseDto_WhenUserExists() {
        // Arrange
        String cpf = "12345678901";
        when(userRepository.findByCpf(cpf)).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.findByCpf(cpf);

        // Assert
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userResponseDto);

        verify(userRepository, times(1)).findByCpf(cpf);
        verify(mapper, times(1)).mapTo(user);
    }

    @Test
    @DisplayName("findByCpf deve lançar UserNotFoundException quando usuário não existe")
    void findByCpf_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        String cpf = "99999999999";
        when(userRepository.findByCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> userService.findByCpf(cpf))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("cpf")
                .hasMessageContaining(cpf);

        verify(userRepository, times(1)).findByCpf(cpf);
        verify(mapper, never()).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findByQueryName deve retornar lista de UserResponseDto quando encontrar usuários")
    void findByQueryName_ShouldReturnUserResponseDtoList_WhenUsersFound() {
        // Arrange
        String name = "João";
        when(userRepository.queryByNameLike(name)).thenReturn(List.of(user));
        when(mapper.mapTo(user)).thenReturn(userResponseDto);

        // Act
        List<UserResponseDto> result = userService.findByQueryName(name);

        // Assert
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(result.get(0))
                .usingRecursiveComparison()
                .isEqualTo(userResponseDto);

        verify(userRepository, times(1)).queryByNameLike(name);
        verify(mapper, times(1)).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findByQueryName deve retornar lista vazia quando nenhum usuário for encontrado")
    void findByQueryName_ShouldReturnEmptyList_WhenNoUsersFound() {
        // Arrange
        String name = "Nome Inexistente";
        when(userRepository.queryByNameLike(name)).thenReturn(List.of());

        // Act
        List<UserResponseDto> result = userService.findByQueryName(name);

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(userRepository, times(1)).queryByNameLike(name);
        verify(mapper, never()).mapTo(any(User.class));
    }

    @Test
    @DisplayName("findByQueryName deve retornar múltiplos usuários quando encontrar mais de um")
    void findByQueryName_ShouldReturnMultipleUsers_WhenFoundMultiple() {
        // Arrange
        String name = "Silva";
        User secondUser = User.builder()
                .id(2L)
                .name("Maria Silva")
                .cpf("98765432100")
                .email("maria@email.com")
                .phone("11988888888")
                .address("Rua Teste, 456")
                .dataRegister(LocalDateTime.now())
                .build();

        UserResponseDto secondUserDto = UserResponseDto.builder()
                .id(2L)
                .name("Maria Silva")
                .cpf("98765432100")
                .email("maria@email.com")
                .phone("11988888888")
                .address("Rua Teste, 456")
                .dataRegister(secondUser.getDataRegister())
                .build();

        when(userRepository.queryByNameLike(name)).thenReturn(List.of(user, secondUser));
        when(mapper.mapTo(user)).thenReturn(userResponseDto);
        when(mapper.mapTo(secondUser)).thenReturn(secondUserDto);

        // Act
        List<UserResponseDto> result = userService.findByQueryName(name);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(UserResponseDto::getName)
                .containsExactlyInAnyOrder("João Silva", "Maria Silva");

        verify(userRepository, times(1)).queryByNameLike(name);
        verify(mapper, times(2)).mapTo(any(User.class));
    }

    @Test
    @DisplayName("save deve retornar UserResponseDto quando sucesso")
    void save_ShouldReturnUserResponseDto_WhenSuccessful() {
        // Arrange
        when(userRepository.findByCpf(userRequestDto.getCpf())).thenReturn(null);
        when(mapper.mapFrom(userRequestDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.save(userRequestDto);

        // Assert
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userResponseDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo(userRequestDto.getName().toLowerCase());
        assertThat(savedUser.getDataRegister()).isNotNull();

        verify(userRepository, times(1)).findByCpf(userRequestDto.getCpf());
        verify(mapper, times(1)).mapFrom(userRequestDto);
        verify(userRepository, times(1)).save(any(User.class));
        verify(mapper, times(1)).mapTo(any(User.class));
    }

    @Test
    @DisplayName("save deve lançar UserAlreadyExistsException quando CPF já existe")
    void save_ShouldThrowUserAlreadyExistsException_WhenCpfAlreadyExists() {
        // Arrange
        when(userRepository.findByCpf(userRequestDto.getCpf())).thenReturn(user);

        // Act & Assert
        assertThatThrownBy(() -> userService.save(userRequestDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("cpf")
                .hasMessageContaining(userRequestDto.getCpf());

        verify(userRepository, times(1)).findByCpf(userRequestDto.getCpf());
        verify(mapper, never()).mapFrom(any(UserRequestDto.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("update deve retornar UserResponseDto quando sucesso")
    void update_ShouldReturnUserResponseDto_WhenSuccessful() {
        // Arrange
        String cpf = "12345678901";
        when(userRepository.findByCpf(cpf)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.mapTo(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.update(userRequestDto, cpf);

        // Assert
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userResponseDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getName()).isEqualTo(userRequestDto.getName().toLowerCase());
        assertThat(updatedUser.getEmail()).isEqualTo(userRequestDto.getEmail());
        assertThat(updatedUser.getPhone()).isEqualTo(userRequestDto.getPhone());
        assertThat(updatedUser.getAddress()).isEqualTo(userRequestDto.getAddress());

        verify(userRepository, times(1)).findByCpf(cpf);
        verify(userRepository, times(1)).save(any(User.class));
        verify(mapper, times(1)).mapTo(any(User.class));
    }

    @Test
    @DisplayName("update deve lançar UserNotFoundException quando usuário não existe")
    void update_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        String cpf = "99999999999";
        when(userRepository.findByCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> userService.update(userRequestDto, cpf))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("cpf")
                .hasMessageContaining(cpf);

        verify(userRepository, times(1)).findByCpf(cpf);
        verify(userRepository, never()).save(any(User.class));
        verify(mapper, never()).mapTo(any(User.class));
    }

    @Test
    @DisplayName("delete deve excluir usuário quando sucesso")
    void delete_ShouldDeleteUser_WhenSuccessful() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.delete(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("delete deve lançar UserNotFoundException quando usuário não existe")
    void delete_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }
} 