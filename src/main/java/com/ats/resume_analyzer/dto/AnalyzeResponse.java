package com.ats.resume_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.List;

@Data
@AllArgsConstructor
public class AnalyzeResponse {

    private Integer finalScore;
    private Integer semanticScore;
    private Integer keywordScore;

    private Set<String> matchedKeywords;
    private Set<String> missingKeywords;

    private List<String> aiMissingSkills;
    private List<String> aiSuggestions;
}