package com.mjh.focustrainer.focus.repository;

import com.mjh.focustrainer.focus.entity.FocusDiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FocusDiaryRepository extends JpaRepository<FocusDiary,Long> {

    List<FocusDiary> findByUserIdOrderByCreatedAtDesc(Long userId);
}
