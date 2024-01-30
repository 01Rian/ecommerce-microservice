package com.ecommerce.userapi.repository;

import com.ecommerce.userapi.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByCpf(String cpf);

    List<UserEntity> queryByNomeLike(String name);
}
