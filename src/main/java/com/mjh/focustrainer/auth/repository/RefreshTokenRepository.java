package com.mjh.focustrainer.auth.repository;

import com.mjh.focustrainer.auth.entity.RefreshToken;
import com.mjh.focustrainer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
}
