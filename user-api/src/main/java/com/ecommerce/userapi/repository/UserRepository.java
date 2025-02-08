package com.ecommerce.userapi.repository;

import com.ecommerce.userapi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo CPF
     * @param cpf CPF do usuário
     * @return User se encontrado, null caso contrário
     */
    Optional<User> findByCpf(String cpf);

    /**
     * Verifica se existe um usuário com o CPF informado
     * @param cpf CPF do usuário
     * @return true se existir, false caso contrário
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca usuários pelo nome (case insensitive, busca parcial)
     * @param name Nome ou parte do nome do usuário
     * @return Lista de usuários que contêm o nome especificado
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> queryByNameLike(@Param("name") String name);
}
