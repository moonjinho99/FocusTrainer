package com.mjh.focustrainer.focus.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FocusRecordResponse {

    private Long diaryId;          // FocusDiary ID
    private String name;           // 훈련 이름
    private LocalDateTime createdAt; // 생성일시

    private Double focusPercent;   // 집중도
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalSecond;
    private Integer targetMinute;
}