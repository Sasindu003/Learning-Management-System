package com.lms.lms.config;

import com.lms.lms.filter.InactivityFilter;
import com.lms.lms.service.InactivityTrackingService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Wires up the inactivity-based auto-shutdown mechanism:
 *
 * <ul>
 *   <li>Enables Spring's {@code @Scheduled} support via {@link EnableScheduling}.</li>
 *   <li>Registers {@link InactivityFilter} for every URL pattern so that each
 *       HTTP request resets the idle timer.</li>
 * </ul>
 *
 * <p>The actual idle-check logic and shutdown trigger live in
 * {@link InactivityTrackingService}.</p>
 */
@Configuration
@EnableScheduling
public class InactivityConfig {

    /**
     * Registers {@link InactivityFilter} with the highest precedence so the
     * inactivity timestamp is updated before any other filter (including
     * Spring Security) processes the request.
     */
    @Bean
    public FilterRegistrationBean<InactivityFilter> inactivityFilterRegistration(
            InactivityTrackingService inactivityTrackingService) {

        InactivityFilter filter = new InactivityFilter(inactivityTrackingService);

        FilterRegistrationBean<InactivityFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Integer.MIN_VALUE); // highest priority
        registration.setName("inactivityFilter");
        return registration;
    }
}
