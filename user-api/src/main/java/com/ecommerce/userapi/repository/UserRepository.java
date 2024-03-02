package com.ecommerce.userapi.repository;

import com.ecommerce.userapi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByCpf(String cpf);

    List<User> queryByNameLike(String name);
}
