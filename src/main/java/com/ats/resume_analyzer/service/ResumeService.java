package com.ats.resume_analyzer.service;

import com.ats.resume_analyzer.dto.ResumeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ats.resume_analyzer.model.Resume;
import com.ats.resume_analyzer.model.User;
import com.ats.resume_analyzer.repository.ResumeRepository;
import com.ats.resume_analyzer.repository.UserRepository;
import com.ats.resume_analyzer.utils.ResumeParserUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public Long uploadResume(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String extractedText = ResumeParserUtil.extractText(file);

        Resume resume = Resume.builder()
                .fileName(file.getOriginalFilename())
                .resumeText(extractedText)
                .uploadedAt(LocalDateTime.now())
                .user(user)
                .build();

        resumeRepository.save(resume);

        return resume.getId();
    }
    public List<ResumeResponse> getMyResumes() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return resumeRepository.findByUser(user)
                .stream()
                .map(resume -> new ResumeResponse(
                        resume.getId(),
                        resume.getFileName(),
                        resume.getUploadedAt()
                ))
                .toList();
    }
    public void deleteResume(Long resumeId){
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        if(!resume.getUser().getId().equals(user.getId())){
            throw new RuntimeException("You are not allowed to delete this resume");
        }
        resumeRepository.delete(resume);
    }
}
