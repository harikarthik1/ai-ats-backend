package com.ats.resume_analyzer.service;

import com.ats.resume_analyzer.dto.*;
import com.ats.resume_analyzer.model.*;
import com.ats.resume_analyzer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;
    private final AiService aiService;

    public AnalyzeResponse analyzeResume(AnalyzeRequest request) {

        User user = getAuthenticatedUser();
        Resume resume = validateResumeOwnership(request.getResumeId(), user);

        String resumeText = resume.getResumeText();

        List<String> jdSkills = aiService.extractSkillsFromJD(request.getJobDescription());

        Set<String> resumeSkills = extractSkillsFromResume(resumeText, jdSkills);

        Set<String> matched = new HashSet<>(resumeSkills);
        Set<String> missing = new HashSet<>(jdSkills);
        missing.removeAll(resumeSkills);

        int keywordScore = computeKeywordScore(matched.size(), jdSkills.size());

        AiService.SemanticResult aiResult =
                aiService.semanticEvaluation(resumeText, request.getJobDescription());

        int semanticScore = aiResult.getSemanticScore();

        int finalScore = computeWeightedFinalScore(keywordScore, semanticScore);

        Analysis analysis = Analysis.builder()
                .jobDescription(request.getJobDescription())
                .score(finalScore)
                .semanticScore(semanticScore)
                .matchedKeywords(String.join(",", matched))
                .missingKeywords(String.join(",", missing))
                .aiMissingSkills(String.join(",", aiResult.getMissingSkills()))
                .aiSuggestions(String.join(" | ", aiResult.getSuggestions()))
                .analyzedAt(LocalDateTime.now())
                .user(user)
                .resume(resume)
                .build();

        analysisRepository.save(analysis);

        return new AnalyzeResponse(
                finalScore,
                semanticScore,
                keywordScore,
                matched,
                missing,
                aiResult.getMissingSkills(),
                aiResult.getSuggestions()
        );
    }


    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Resume validateResumeOwnership(Long resumeId, User user) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return resume;
    }

    private Set<String> extractSkillsFromResume(String resumeText, List<String> jdSkills) {

        String lowerResume = resumeText.toLowerCase();
        Set<String> found = new HashSet<>();

        for (String skill : jdSkills) {
            if (lowerResume.contains(skill.toLowerCase())) {
                found.add(skill);
            }
        }

        return found;
    }

    private int computeKeywordScore(int matched, int total) {

        if (total == 0) return 0;

        int score = (matched * 100) / total;

        if (matched == 0) return 20;
        if (matched < total / 2) return Math.min(score, 60);

        return score;
    }

    private int computeWeightedFinalScore(int keywordScore, int semanticScore) {

        double weighted =
                (keywordScore * 0.4) +
                        (semanticScore * 0.6);

        return (int) Math.round(weighted);
    }
    public List<AnalysisHistoryResponse> getMyAnalysisHistory() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return analysisRepository.findByUser(user)
                .stream()
                .map(analysis -> new AnalysisHistoryResponse(
                        analysis.getId(),
                        analysis.getResume().getId(),
                        analysis.getResume().getFileName(),
                        analysis.getScore(),              // final score
                        analysis.getSemanticScore(),
                        analysis.getAnalyzedAt()
                ))
                .toList();
    }
}