package com.mjh.focustrainer.focus.repository;

import com.mjh.focustrainer.focus.entity.FocusDiary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FocusDiaryRepository extends JpaRepository<FocusDiary,Long> {
}
