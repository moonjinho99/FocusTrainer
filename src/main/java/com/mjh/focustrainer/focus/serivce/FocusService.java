package com.mjh.focustrainer.focus.serivce;

import com.mjh.focustrainer.common.exception.CustomException;
import com.mjh.focustrainer.common.jwt.JwtProvider;
import com.mjh.focustrainer.common.response.ApiResponse;
import com.mjh.focustrainer.common.response.ErrorCode;
import com.mjh.focustrainer.focus.dto.FocusRecordResponse;
import com.mjh.focustrainer.focus.dto.FocusSaveRequest;
import com.mjh.focustrainer.focus.entity.FocusDetail;
import com.mjh.focustrainer.focus.entity.FocusDiary;
import com.mjh.focustrainer.focus.repository.FocusDetailRepository;
import com.mjh.focustrainer.focus.repository.FocusDiaryRepository;
import com.mjh.focustrainer.user.entity.User;
import com.mjh.focustrainer.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FocusService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final FocusDiaryRepository focusDiaryRepository;
    private final FocusDetailRepository focusDetailRepository;

    public ResponseEntity<ApiResponse<Void>> save(FocusSaveRequest request, HttpServletRequest httpReqeust)
    {
        String header = httpReqeust.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String token = header.substring(7);
        Long userId = jwtProvider.getUserId(token);

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = userRepository.getReferenceById(userId);

        FocusDetail focusDetail = FocusDetail.builder()
                .focusPercent(request.getFocusPercent())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalSecond(request.getTotalSeconds())
                .targetMinute(request.getTargetMinute())
                .build();

        FocusDiary diary = FocusDiary.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .name(request.getName())
                .build();

        diary.setDetail(focusDetail);
        focusDiaryRepository.save(diary);

        return ResponseEntity.ok(ApiResponse.ok("훈련 기록 저장 완료"));
    }

    public ResponseEntity<ApiResponse<List<FocusRecordResponse>>> getRecords(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String token = header.substring(7);
        Long userId = jwtProvider.getUserId(token);

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<FocusDiary> diaries = focusDiaryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<FocusRecordResponse> lists = diaries.stream()
            .map(diary -> {
                FocusDetail detail = diary.getDetail();
                return FocusRecordResponse.builder()
                        .diaryId(diary.getId())
                        .name(diary.getName())
                        .createdAt(diary.getCreatedAt())
                        .focusPercent(detail != null ? detail.getFocusPercent() : null)
                        .startTime(detail != null ? detail.getStartTime() : null)
                        .endTime(detail != null ? detail.getEndTime() : null)
                        .totalSecond(detail != null ? detail.getTotalSecond() : null)
                        .targetMinute(detail != null ? detail.getTargetMinute() : null)
                        .build();
            })
            .toList();


        System.out.println(lists.toString());

        return ResponseEntity.ok(ApiResponse.ok("훈련 기록 조회 성공",lists));
    }
}
