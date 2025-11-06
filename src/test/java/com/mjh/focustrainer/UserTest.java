package com.mjh.focustrainer;


import com.mjh.focustrainer.auth.repository.UserRepository;
import com.mjh.focustrainer.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;
@DataJpaTest
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User 엔티티 저장 및 조회가 정상 동작해야 한다.")
    void saveAndFlushUser() {

        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("테스터")
                .build();

        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getEmail()).isEqualTo("test@example.com");
        assertThat(found.getNickname()).isEqualTo("테스터");
        assertThat(found.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("이메일은 유일해야 한다 (unique 제약조건 위반 시 예외 발생)")
    void uniqueEmailConstraint() {

        User user1 = User.builder()
                .email("dup@example.com")
                .password("pwd1")
                .nickname("user1")
                .build();

        User user2 = User.builder()
                .email("dup@example.com")
                .password("pwd2")
                .nickname("user2")
                .build();

        userRepository.save(user1);

        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("null 필드가 있을 경우 제약조건 위반 예외가 발생해야 한다")
    void notNullConstraint() {

        User invalidUser = User.builder()
                .email(null)
                .password("abc")
                .nickname("user")
                .build();

        assertThatThrownBy(()-> userRepository.saveAndFlush(invalidUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
