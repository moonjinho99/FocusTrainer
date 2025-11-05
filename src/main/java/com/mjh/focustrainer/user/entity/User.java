package com.mjh.focustrainer.user.entity;
import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String email;

    @Column(nullable=false, length=100)
    private String password;

    @Column(nullable=false, length=100)
    private String nickname;
}
