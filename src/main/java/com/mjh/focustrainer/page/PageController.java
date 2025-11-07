package com.mjh.focustrainer.page;


import com.mjh.focustrainer.common.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final JwtProvider jwtProvider;

    @GetMapping("/auth/login")
    public String loginPage()
    {
        return "/auth/login";
    }

    @GetMapping("/auth/signup")
    public String signupPage()
    {
        return "/auth/signup";
    }

    @GetMapping("/focus/main")
    public String focusPage()
    {
        return "/main/focus-main";
    }

    @GetMapping("/focus/start")
    public String focusStartPage(@RequestParam(name = "minutes", required = false) Integer minutes, Model model) {
        model.addAttribute("minutes", minutes);
        return "focus/focus-start";
    }

    // ✅ 인증 검사용 API (fetch 요청)
    @GetMapping("/api/focus/authorize")
    @ResponseBody
    public ResponseEntity<Void> authorizeFocus(@RequestParam Integer minutes, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        if (!jwtProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 유효하면 200 OK
        return ResponseEntity.ok().build();
    }

}
