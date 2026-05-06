package com.ats.resume_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.List;

@Data
@AllArgsConstructor
public class AnalyzeResponse {

    private Long analysisId;
    private Integer score;
    private List<String> skills;
    private List<String> recommendations;
}