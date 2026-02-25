package com.ats.resume_analyzer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ats.resume_analyzer.service.ResumeService;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) throws IOException {

        Long resumeId = resumeService.uploadResume(file);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Resume uploaded successfully",
                        "resumeId", resumeId
                )
        );
    }
    @GetMapping("/my-resumes")
    public ResponseEntity<?> getMyResumes() {

        return ResponseEntity.ok(resumeService.getMyResumes());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id) {

        resumeService.deleteResume(id);

        return ResponseEntity.ok(
                Map.of("message", "Resume deleted successfully")
        );
    }
}
