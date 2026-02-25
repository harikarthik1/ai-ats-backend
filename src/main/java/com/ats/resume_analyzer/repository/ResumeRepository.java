package com.ats.resume_analyzer.repository;

import com.ats.resume_analyzer.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ats.resume_analyzer.model.User;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUser(User user);
}
