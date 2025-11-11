package com.mjh.focustrainer.focus.controller;


import com.mjh.focustrainer.common.response.ApiResponse;
import com.mjh.focustrainer.focus.dto.FocusRecordResponse;
import com.mjh.focustrainer.focus.dto.FocusSaveRequest;
import com.mjh.focustrainer.focus.serivce.FocusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Focus", description = "집중력 훈련 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/focus")
public class FocusController {

    private final FocusService focusService;
    @Operation(summary = "훈련 기록 저장", description = "훈련 기록을 저장합니다.")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Void>> save(@Valid @RequestBody FocusSaveRequest request, HttpServletRequest httpRequest){

        return focusService.save(request,httpRequest);
    }

    @Operation(summary = "훈련 기록 조회", description = "훈련 기록을 조회합니다.")
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<FocusRecordResponse>>> getRecords(HttpServletRequest request)
    {
        return focusService.getRecords(request);
    }

}
