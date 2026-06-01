package com.lms.lms.filter;

import com.lms.lms.service.InactivityTrackingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that notifies {@link InactivityTrackingService} of every
 * incoming HTTP request, resetting the inactivity clock before the request
 * is dispatched to the rest of the filter chain.
 *
 * <p>Registered as a Spring-managed bean via
 * {@link com.lms.lms.config.InactivityConfig}.</p>
 */
@RequiredArgsConstructor
public class InactivityFilter extends OncePerRequestFilter {

    private final InactivityTrackingService inactivityTrackingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        inactivityTrackingService.recordRequest();
        filterChain.doFilter(request, response);
    }
}
