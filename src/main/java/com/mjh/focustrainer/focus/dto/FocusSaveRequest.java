package com.mjh.focustrainer.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FocusSaveRequest {

    @Schema(description = "훈련명", example = "집중력 훈련 1일차")
    @NotBlank(message = "훈련명은 필수 입력값입니다.")
    private String name;

    @Schema(description = "훈련 시작시간", example = "2025-11-10 10:00:00")
    @NotBlank(message = "훈련 시작시간은 필수 입력값입니다.")
    private LocalDateTime startTime;

    @Schema(description = "훈련 종료시간", example = "2025-11-10 10:30:00")
    @NotBlank(message = "훈련 종료시간은 필수 입력값입니다.")
    private LocalDateTime endTime;

    @Schema(description = "목표시간", example = "30")
    @NotBlank(message = "목표시간은 필수 입력값입니다.")
    private int targetMinute;

    @Schema(description = "훈련시간", example = "1800")
    @NotBlank(message = "훈련시간은 필수 입력값입니다.")
    private int totalSeconds;

    @Schema(description = "집중도", example = "95")
    @NotBlank(message = "집중도는 필수 입력값입니다.")
    private double focusPercent;
}
