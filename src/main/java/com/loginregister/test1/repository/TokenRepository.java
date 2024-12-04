package com.loginregister.test1.repository;

import com.loginregister.test1.entities.TokenEntity;
import com.loginregister.test1.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<TokenEntity, Long> {

    List<TokenEntity> findByUserIdAndExpiredFalseAndRevokedFalse(Long userId);

    Optional<TokenEntity> findByToken(String token);
}
