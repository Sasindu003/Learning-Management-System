package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final CourseService courseService;
    private final AssignmentService assignmentService;

    @GetMapping("/view/material/{id}")
    public ResponseEntity<Void> viewMaterial(@PathVariable("id") Long id) {
        CourseMaterial material = courseService.findMaterialById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        if (material.getFilePath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Material has no file");
        }

        return redirectToFile(material.getFilePath());
    }

    @GetMapping("/view/assignment/{id}")
    public ResponseEntity<Void> viewAssignment(@PathVariable("id") Long id) {
        Assignment assignment = assignmentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        if (assignment.getAttachmentPath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment has no attachment");
        }

        return redirectToFile(assignment.getAttachmentPath());
    }

    @GetMapping("/view/submission/{id}")
    public ResponseEntity<Void> viewSubmission(@PathVariable("id") Long id) {
        AssignmentSubmission submission = assignmentService.getSubmissionById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        if (submission.getFilePath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submission has no file");
        }

        return redirectToFile(submission.getFilePath());
    }

    private ResponseEntity<Void> redirectToFile(String filePath) {
        String publicUrl = fileStorageService.getPublicUrl(filePath);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(publicUrl))
                .build();
    }
}
