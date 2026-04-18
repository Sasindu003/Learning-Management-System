package com.lms.lms.controller;

import com.lms.lms.model.*;
import com.lms.lms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Path;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final CourseService courseService;
    private final AssignmentService assignmentService;

    @GetMapping("/view/material/{id}")
    public ResponseEntity<Resource> viewMaterial(@PathVariable("id") Long id) {
        CourseMaterial material = courseService.findMaterialById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        if (material.getFilePath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Material has no file");
        }

        return serveFile(material.getFilePath(), material.getFileName(), material.getFileType());
    }

    @GetMapping("/view/assignment/{id}")
    public ResponseEntity<Resource> viewAssignment(@PathVariable("id") Long id) {
        Assignment assignment = assignmentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        if (assignment.getAttachmentPath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment has no attachment");
        }

        return serveFile(assignment.getAttachmentPath(), assignment.getAttachmentName(), null);
    }

    @GetMapping("/view/submission/{id}")
    public ResponseEntity<Resource> viewSubmission(@PathVariable("id") Long id) {
        AssignmentSubmission submission = assignmentService.getSubmissionById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        if (submission.getFilePath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submission has no file");
        }

        return serveFile(submission.getFilePath(), submission.getFileName(), null);
    }

    private ResponseEntity<Resource> serveFile(String filePath, String originalName, String contentType) {
        try {
            Path path = fileStorageService.load(filePath);
            Resource resource = new UrlResource(path.toUri());

            // Bug fix: use AND (&&) — file must BOTH exist AND be readable to be served safely
            if (resource.exists() && resource.isReadable()) {
                String type = contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE;
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(type))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + originalName + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or not readable");
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading file");
        }
    }
}
