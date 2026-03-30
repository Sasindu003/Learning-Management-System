package com.lms.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ChangelogController {

    @GetMapping("/changelog")
    public String changelog(Model model) {
        try {
            Path path = Paths.get("logs", "CHANGELOG.md");
            String content = Files.readString(path);
            model.addAttribute("changelogContent", content);
        } catch (IOException e) {
            model.addAttribute("changelogContent", "# Error\nCould not load changelog: " + e.getMessage());
        }
        return "changelog";
    }
}
