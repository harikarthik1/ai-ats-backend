package com.ats.resume_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnalysisHistoryResponse {

    private Long id;
    private Long resumeId;
    private String resumeFileName;
    private int finalScore;
    private int semanticScore;
    private LocalDateTime analyzedAt;
}