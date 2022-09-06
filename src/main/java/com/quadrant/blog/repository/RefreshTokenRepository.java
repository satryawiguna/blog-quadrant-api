package com.quadrant.blog.repository;

import com.quadrant.blog.entity.RefreshTokenEntity;
import com.quadrant.blog.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    int deleteByToken(String token);

}
