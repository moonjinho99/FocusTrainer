package com.mjh.focustrainer.focus.entity;

import com.mjh.focustrainer.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "focus_diary")
public class FocusDiary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private String name;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private FocusDetail detail;

    // 편의 메서드 (양방향 연관관계 동기화)
    public void setDetail(FocusDetail detail) {
        this.detail = detail;
        detail.setDiary(this);
    }
}
