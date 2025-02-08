package com.ecommerce.userapi.repository;

import com.ecommerce.userapi.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    private final UserRepository userRepository;
    private final TestEntityManager entityManager;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, TestEntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    private User createUser(String name, String cpf) {
        return User.builder()
                .name(name.toLowerCase())
                .cpf(cpf)
                .email(name.toLowerCase().replace(" ", ".") + "@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .dataRegister(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("findByCpf deve retornar Optional<User> quando sucesso")
    void findByCpf_ShouldReturnOptionalUser_WhenSuccessful() {
        // Arrange
        String cpf = "12345678901";
        User userToSave = createUser("João Silva", cpf);
        entityManager.persist(userToSave);

        // Act
        Optional<User> result = userRepository.findByCpf(cpf);

        // Assert
        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userToSave);
    }

    @Test
    @DisplayName("findByCpf deve retornar Optional vazio quando cpf não existe")
    void findByCpf_ShouldReturnEmptyOptional_WhenCpfDoesNotExist() {
        // Act
        Optional<User> result = userRepository.findByCpf("99999999999");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByCpf deve retornar true quando cpf existe")
    void existsByCpf_ShouldReturnTrue_WhenCpfExists() {
        // Arrange
        String cpf = "12345678901";
        User userToSave = createUser("João Silva", cpf);
        entityManager.persist(userToSave);

        // Act
        boolean exists = userRepository.existsByCpf(cpf);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByCpf deve retornar false quando cpf não existe")
    void existsByCpf_ShouldReturnFalse_WhenCpfDoesNotExist() {
        // Act
        boolean exists = userRepository.existsByCpf("99999999999");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("queryByNameLike deve retornar lista de users quando nome existe")
    void queryByNameLike_ShouldReturnUserList_WhenNameExists() {
        // Arrange
        User user1 = createUser("João Silva", "12345678901");
        User user2 = createUser("Maria Silva", "98765432100");
        User user3 = createUser("Pedro Santos", "11122233344");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // Act
        List<User> result = userRepository.queryByNameLike("Silva");

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("joão silva", "maria silva");
    }

    @Test
    @DisplayName("queryByNameLike deve retornar lista vazia quando nome não existe")
    void queryByNameLike_ShouldReturnEmptyList_WhenNameDoesNotExist() {
        // Arrange
        User user = createUser("João Silva", "12345678901");
        entityManager.persist(user);

        // Act
        List<User> result = userRepository.queryByNameLike("Santos");

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save deve persistir user quando sucesso")
    void save_ShouldPersistUser_WhenSuccessful() {
        // Arrange
        User userToSave = createUser("João Silva", "12345678901");

        // Act
        User result = userRepository.save(userToSave);

        // Assert
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userToSave);

        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("save deve atualizar user quando sucesso")
    void save_ShouldUpdateUser_WhenSuccessful() {
        // Arrange
        User userToSave = createUser("João Silva", "12345678901");
        entityManager.persist(userToSave);

        userToSave.setName("João Silva Updated");
        userToSave.setEmail("joao.updated@email.com");

        // Act
        User result = userRepository.save(userToSave);

        // Assert
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userToSave);

        User foundUser = entityManager.find(User.class, result.getId());
        assertThat(foundUser.getName()).isEqualTo("João Silva Updated");
        assertThat(foundUser.getEmail()).isEqualTo("joao.updated@email.com");
    }

    @Test
    @DisplayName("delete deve remover user quando sucesso")
    void delete_ShouldRemoveUser_WhenSuccessful() {
        // Arrange
        User userToDelete = createUser("João Silva", "12345678901");
        entityManager.persist(userToDelete);

        // Act
        userRepository.delete(userToDelete);

        // Assert
        User foundUser = entityManager.find(User.class, userToDelete.getId());
        assertThat(foundUser).isNull();
    }

    @Test
    @DisplayName("findById deve retornar Optional<User> quando sucesso")
    void findById_ShouldReturnOptionalUser_WhenSuccessful() {
        // Arrange
        User userToFind = createUser("João Silva", "12345678901");
        entityManager.persist(userToFind);

        // Act
        Optional<User> result = userRepository.findById(userToFind.getId());

        // Assert
        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userToFind);
    }

    @Test
    @DisplayName("findById deve retornar Optional.empty quando id não existe")
    void findById_ShouldReturnOptionalEmpty_WhenIdDoesNotExist() {
        // Act
        Optional<User> result = userRepository.findById(1L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll com Pageable deve retornar Page<User> quando sucesso")
    void findAll_WithPageable_ShouldReturnPageUser_WhenSuccessful() {
        // Arrange
        User user1 = createUser("João Silva", "12345678901");
        User user2 = createUser("Maria Santos", "98765432100");
        User user3 = createUser("Pedro Souza", "11122233344");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));

        // Act
        Page<User> result = userRepository.findAll(pageRequest);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2);

        assertThat(result.getContent())
                .extracting(User::getName)
                .containsExactly("joão silva", "maria santos");

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }
} 