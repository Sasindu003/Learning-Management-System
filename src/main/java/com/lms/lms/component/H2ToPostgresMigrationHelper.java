package com.lms.lms.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A one-time utility to help migrate from H2 to Postgres.
 * To use: 
 * 1. Start the app.
 * 2. It will look for 'generate_migration.txt' in the project root.
 * 3. If found, it will dump the H2 database into 'migration.sql'.
 */
@Component
@Slf4j
public class H2ToPostgresMigrationHelper implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        File flagFile = new File("generate_migration.txt");
        if (flagFile.exists()) {
            log.info("Migration flag found! Exporting H2 data to migration.sql...");
            
            try {
                // H2 command to dump the entire database to a SQL file
                // We use 'SCRIPT' command which is built into H2
                jdbcTemplate.execute("SCRIPT TO 'migration.sql' COLUMNS");
                
                log.info("Successfully exported H2 data to 'migration.sql'");
                log.warn("IMPORTANT: Read migration.sql and remove H2-specific lines (like CREATE USER, SETING, etc.) before importing to Supabase.");
                
                // Remove the flag file so it doesn't run again
                flagFile.delete();
            } catch (Exception e) {
                log.error("Failed to export H2 database", e);
            }
        }
    }
}
