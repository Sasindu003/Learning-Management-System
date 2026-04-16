package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final UserService userService;
    private final CourseService courseService;
    private final StudentGradeService studentGradeService;
    private final AssignmentService assignmentService;

    @GetMapping("/@{username}")
    public String viewPortfolio(@PathVariable("username") String username, Model model) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);

        if (user.getRole() == User.Role.TEACHER) {
            List<Course> courses = courseService.findByTeacher(user);
            model.addAttribute("courses", courses);
            model.addAttribute("courseCount", courses.size());
        } else if (user.getRole() == User.Role.STUDENT) {
            model.addAttribute("grade", user.getGrade());
            model.addAttribute("gpa", studentGradeService.calculateGPA(user));
            
            // Calculate some basic stats
            List<Course> courses = user.getGrade() != null ? courseService.findByGrade(user.getGrade()) : Collections.emptyList();
            long totalAssignments = courses.stream()
                    .mapToLong(c -> assignmentService.findByCourse(c).size())
                    .sum();
            model.addAttribute("totalAssignments", totalAssignments);
        }

        return "portfolio";
    }
}
