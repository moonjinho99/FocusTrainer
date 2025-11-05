package com.mjh.focustrainer.page;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
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
}
