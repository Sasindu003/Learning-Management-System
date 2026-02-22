package com.lms.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthController — handles the login page.
 *
 * WHY a separate controller for login?
 * - Spring Security handles the actual authentication (POST /login).
 * - We only need to serve the login HTML page (GET /login).
 * - Keeping it separate makes the code easy to find and understand.
 */
@Controller
public class AuthController {

    /**
     * Show the login page.
     * Spring Security automatically handles the form submission (POST /login).
     * We just need to return the template name.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Renders templates/login.html
    }
}
