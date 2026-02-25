package com.ats.resume_analyzer.repository;

import com.ats.resume_analyzer.model.Analysis;
import com.ats.resume_analyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByUser(User user);
}
