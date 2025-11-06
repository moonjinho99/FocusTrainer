package com.mjh.focustrainer.auth.repository;

import com.mjh.focustrainer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
