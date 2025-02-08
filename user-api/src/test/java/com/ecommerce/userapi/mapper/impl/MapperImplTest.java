package com.ecommerce.userapi.mapper.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.ecommerce.userapi.domain.dto.UserRequestDto;
import com.ecommerce.userapi.domain.dto.UserResponseDto;
import com.ecommerce.userapi.domain.entity.User;

@DisplayName("MapperImpl - Testes Unitários")
class MapperImplTest {

    // System Under Test
    private MapperImpl sut;
    private ModelMapper modelMapper;

    private User user;
    private UserResponseDto userResponseDto;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        // Arrange
        modelMapper = new ModelMapper();
        sut = new MapperImpl(modelMapper);

        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .dataRegister(now)
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .dataRegister(now)
                .build();

        userRequestDto = UserRequestDto.builder()
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .build();
    }

    @Nested
    @DisplayName("Testes do método mapTo")
    class MapToTests {
        
        @Test
        @DisplayName("deve retornar UserResponseDto quando User possuir todos os campos preenchidos")
        void mapTo_ShouldReturnUserResponseDto_WhenUserHasAllFields() {
            // Act
            UserResponseDto result = sut.mapTo(user);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(userResponseDto);
        }

        @Test
        @DisplayName("deve retornar UserResponseDto com campos nulos quando User estiver vazio")
        void mapTo_ShouldReturnUserResponseDtoWithNullFields_WhenUserIsEmpty() {
            // Arrange
            User emptyUser = User.builder().build();

            // Act
            UserResponseDto result = sut.mapTo(emptyUser);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .hasAllNullFieldsOrProperties()
                    .extracting(
                            UserResponseDto::getId,
                            UserResponseDto::getName,
                            UserResponseDto::getCpf,
                            UserResponseDto::getEmail,
                            UserResponseDto::getPhone,
                            UserResponseDto::getAddress,
                            UserResponseDto::getDataRegister
                    )
                    .containsOnlyNulls();
        }

        @Test
        @DisplayName("deve preservar todos os tipos de dados ao mapear User para UserResponseDto")
        void mapTo_ShouldPreserveDataTypes_WhenMappingUserToUserResponseDto() {
            // Act
            UserResponseDto result = sut.mapTo(user);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(dto -> {
                        assertThat(dto.getId()).isInstanceOf(Long.class);
                        assertThat(dto.getName()).isInstanceOf(String.class);
                        assertThat(dto.getCpf()).isInstanceOf(String.class);
                        assertThat(dto.getEmail()).isInstanceOf(String.class);
                        assertThat(dto.getPhone()).isInstanceOf(String.class);
                        assertThat(dto.getAddress()).isInstanceOf(String.class);
                        assertThat(dto.getDataRegister()).isInstanceOf(LocalDateTime.class);
                    });
        }

        @Test
        @DisplayName("deve mapear User com campos parcialmente preenchidos")
        void mapTo_ShouldMapPartiallyFilledUser_WhenSomeFieldsAreNull() {
            // Arrange
            User partialUser = User.builder()
                    .id(1L)
                    .name("João Silva")
                    .cpf("12345678901")
                    .build();

            // Act
            UserResponseDto result = sut.mapTo(partialUser);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(dto -> {
                        assertThat(dto.getId()).isEqualTo(1L);
                        assertThat(dto.getName()).isEqualTo("João Silva");
                        assertThat(dto.getCpf()).isEqualTo("12345678901");
                        assertThat(dto.getEmail()).isNull();
                        assertThat(dto.getPhone()).isNull();
                        assertThat(dto.getAddress()).isNull();
                        assertThat(dto.getDataRegister()).isNull();
                    });
        }
    }

    @Nested
    @DisplayName("Testes do método mapFrom")
    class MapFromTests {

        @Test
        @DisplayName("deve retornar User quando UserRequestDto possuir todos os campos preenchidos")
        void mapFrom_ShouldReturnUser_WhenUserRequestDtoHasAllFields() {
            // Act
            User result = sut.mapFrom(userRequestDto);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(user -> {
                        assertThat(user.getId()).isNull();
                        assertThat(user.getName()).isEqualTo("João Silva");
                        assertThat(user.getCpf()).isEqualTo("12345678901");
                        assertThat(user.getEmail()).isEqualTo("joao@email.com");
                        assertThat(user.getPhone()).isEqualTo("11999999999");
                        assertThat(user.getAddress()).isEqualTo("Rua Teste, 123");
                        assertThat(user.getDataRegister()).isNull();
                    });
        }

        @Test
        @DisplayName("deve retornar User com campos nulos quando UserRequestDto estiver vazio")
        void mapFrom_ShouldReturnUserWithNullFields_WhenUserRequestDtoIsEmpty() {
            // Arrange
            UserRequestDto emptyDto = UserRequestDto.builder().build();

            // Act
            User result = sut.mapFrom(emptyDto);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .hasAllNullFieldsOrProperties()
                    .extracting(
                            User::getId,
                            User::getName,
                            User::getCpf,
                            User::getEmail,
                            User::getPhone,
                            User::getAddress,
                            User::getDataRegister
                    )
                    .containsOnlyNulls();
        }

        @Test
        @DisplayName("deve preservar todos os tipos de dados ao mapear UserRequestDto para User")
        void mapFrom_ShouldPreserveDataTypes_WhenMappingUserRequestDtoToUser() {
            // Act
            User result = sut.mapFrom(userRequestDto);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(user -> {
                        assertThat(user.getName()).isInstanceOf(String.class);
                        assertThat(user.getCpf()).isInstanceOf(String.class);
                        assertThat(user.getEmail()).isInstanceOf(String.class);
                        assertThat(user.getPhone()).isInstanceOf(String.class);
                        assertThat(user.getAddress()).isInstanceOf(String.class);
                    });
        }

        @Test
        @DisplayName("deve mapear UserRequestDto com campos parcialmente preenchidos")
        void mapFrom_ShouldMapPartiallyFilledUserRequestDto_WhenSomeFieldsAreNull() {
            // Arrange
            UserRequestDto partialDto = UserRequestDto.builder()
                    .name("João Silva")
                    .cpf("12345678901")
                    .build();

            // Act
            User result = sut.mapFrom(partialDto);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(user -> {
                        assertThat(user.getId()).isNull();
                        assertThat(user.getName()).isEqualTo("João Silva");
                        assertThat(user.getCpf()).isEqualTo("12345678901");
                        assertThat(user.getEmail()).isNull();
                        assertThat(user.getPhone()).isNull();
                        assertThat(user.getAddress()).isNull();
                        assertThat(user.getDataRegister()).isNull();
                    });
        }
    }
} 