package com.ats.resume_analyzer.dto;

import lombok.Data;

@Data
public class AnalyzeRequest {
    private Long resumeId;
    private String jobDescription;
}
