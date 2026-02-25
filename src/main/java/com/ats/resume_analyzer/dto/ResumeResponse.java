package com.ats.resume_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResumeResponse {

    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
}