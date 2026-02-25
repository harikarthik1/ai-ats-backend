package com.ats.resume_analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;
    private int score;

    @Column(columnDefinition = "TEXT")
    private String matchedKeywords;

    @Column(columnDefinition = "TEXT")
    private String missingKeywords;

    private int semanticScore;

    @Column(columnDefinition = "TEXT")
    private String aiSuggestions;

    @Column(columnDefinition = "TEXT")
    private String aiMissingSkills;

    private LocalDateTime analyzedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
}
