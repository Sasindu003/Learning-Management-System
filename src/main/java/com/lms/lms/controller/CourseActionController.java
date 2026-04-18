package com.lms.lms.controller;

import com.lms.lms.model.Course;
import com.lms.lms.model.User;
import com.lms.lms.service.CourseService;
import com.lms.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseActionController {

    private final UserService userService;
    private final CourseService courseService;

    @GetMapping("/{id}/toggle-pin")
    public String togglePin(@PathVariable("id") Long id, Authentication auth, RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Course course = courseService.findById(id).orElseThrow();
        
        userService.toggleCoursePin(user, course);
        
        String redirect = "/";
        if (user.getRole() == User.Role.STUDENT) {
            redirect = "/student/courses";
        } else if (user.getRole() == User.Role.TEACHER) {
            redirect = "/teacher/courses";
        }
        
        return "redirect:" + redirect;
    }
}
