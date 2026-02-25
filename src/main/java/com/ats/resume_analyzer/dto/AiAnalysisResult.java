package com.ats.resume_analyzer.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiAnalysisResult {
    private int semanticScore;
    private List<String> missingSkills;
    private List<String> suggestions;
}
