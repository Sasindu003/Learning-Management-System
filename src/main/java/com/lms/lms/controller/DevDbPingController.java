package com.lms.lms.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("supabase")
public class DevDbPingController {

    private final JdbcTemplate jdbcTemplate;

    public DevDbPingController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/dev/db-ping")
    public String ping() {
        Integer one = jdbcTemplate.queryForObject("select 1", Integer.class);
        return (one != null && one == 1) ? "ok" : "unexpected";
    }
}

