package com.ats.resume_analyzer.controller;

import com.ats.resume_analyzer.dto.AnalysisHistoryResponse;
import com.ats.resume_analyzer.dto.AnalyzeRequest;
import com.ats.resume_analyzer.dto.AnalyzeResponse;
import com.ats.resume_analyzer.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;
    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponse> analyzeResume(@RequestBody AnalyzeRequest request){
        AnalyzeResponse response = analysisService.analyzeResume(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/my-history")
    public List<AnalysisHistoryResponse> getHistory() {
        return analysisService.getMyAnalysisHistory();
    }
}
