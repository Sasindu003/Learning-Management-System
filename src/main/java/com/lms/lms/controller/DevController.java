package com.lms.lms.controller;

import com.lms.lms.model.User;
import com.lms.lms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevController {

    private final UserService userService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/quick-login")
    public String quickLogin(HttpServletRequest request, HttpServletResponse response) {
        // Find the default admin user
        User admin = userService.findByUsername("admin").orElse(null);
        if (admin != null) {
            System.out.println("DEBUG: Found admin user. Attempting quick login...");

            // Programmatically authenticate as admin
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    admin.getUsername(), null, authorities);

            // Create and set the security context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            // Explicitly save the context to the repository (session) so it survives
            // redirect
            securityContextRepository.saveContext(context, request, response);

            System.out.println("DEBUG: Authentication successful for " + admin.getUsername() + ". Redirecting...");

            // Redirect directly to admin users dashboard
            return "redirect:/admin/users";
        }
        System.out.println("DEBUG: Admin user NOT found!");
        return "redirect:/login?error=true";
    }
}
