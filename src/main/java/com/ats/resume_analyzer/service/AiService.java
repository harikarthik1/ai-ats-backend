package com.ats.resume_analyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    // =========================================================
    // 1️⃣ Extract Structured Skills From Job Description
    // =========================================================

    public List<String> extractSkillsFromJD(String jobDescription) {

        try {

            String prompt = """
                    You are an ATS system.

                    Extract ONLY hard technical skills from the job description.

                    Ignore soft skills.
                    Ignore generic words.
                    Ignore responsibilities.
                    Ignore filler text.

                    Return ONLY raw JSON array.

                    Example:
                    ["Java","Spring Boot","Microservices"]

                    Job Description:
                    """ + jobDescription;

            String response = callGemini(prompt);

            response = sanitizeResponse(response);

            JsonNode root = objectMapper.readTree(response);

            List<String> skills = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode node : root) {
                    skills.add(node.asText());
                }
            }

            return skills;

        } catch (Exception e) {
            throw new RuntimeException("Skill extraction failed: " + e.getMessage());
        }
    }

    // =========================================================
    // 2️⃣ Semantic Evaluation
    // =========================================================

    public SemanticResult semanticEvaluation(String resumeText, String jobDescription) {

        try {

            String prompt = """
                    You are an advanced ATS system.

                    Evaluate semantic match between resume and job description.

                    Score based on:
                    - Core skills match
                    - Experience alignment
                    - Domain relevance
                    - Technical depth

                    Ignore:
                    - Soft skills
                    - Generic words
                    - Filler content

                    Return ONLY raw JSON.
                    No markdown.
                    No explanation.

                    Format:
                    {
                      "semanticScore": number,
                      "missingSkills": ["skill1","skill2"],
                      "suggestions": ["suggestion1","suggestion2"]
                    }

                    Resume:
                    """ + resumeText + """

                    Job Description:
                    """ + jobDescription;

            String response = callGemini(prompt);

            response = sanitizeResponse(response);

            JsonNode root = objectMapper.readTree(response);

            int semanticScore = root.get("semanticScore").asInt();

            List<String> missingSkills = new ArrayList<>();
            root.get("missingSkills").forEach(node ->
                    missingSkills.add(node.asText())
            );

            List<String> suggestions = new ArrayList<>();
            root.get("suggestions").forEach(node ->
                    suggestions.add(node.asText())
            );

            return new SemanticResult(
                    semanticScore,
                    missingSkills,
                    suggestions
            );

        } catch (Exception e) {
            throw new RuntimeException("Semantic evaluation failed: " + e.getMessage());
        }
    }

    // =========================================================
    // 🔹 Gemini API Call
    // =========================================================

    private String callGemini(String prompt) {

        try {

            String requestBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            { "text": "%s" }
                          ]
                        }
                      ]
                    }
                    """.formatted(prompt.replace("\"", "\\\""));

            String response = webClient.post()
                    .uri(GEMINI_URL + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);

            return root
                    .get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Gemini API call failed: " + e.getMessage());
        }
    }

    // =========================================================
    // 🔹 Sanitize AI Response
    // =========================================================

    private String sanitizeResponse(String response) {

        if (response == null) return "";

        // Remove markdown blocks
        response = response.replaceAll("```json", "");
        response = response.replaceAll("```", "");

        // Extract only JSON portion
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end != -1) {
            response = response.substring(start, end + 1);
        }

        // Handle array-only case
        if (response.trim().startsWith("[")) {
            int arrayEnd = response.lastIndexOf("]");
            response = response.substring(0, arrayEnd + 1);
        }

        return response.trim();
    }

    // =========================================================
    // 🔹 Inner Result Class
    // =========================================================

    public static class SemanticResult {

        private final int semanticScore;
        private final List<String> missingSkills;
        private final List<String> suggestions;

        public SemanticResult(int semanticScore,
                              List<String> missingSkills,
                              List<String> suggestions) {
            this.semanticScore = semanticScore;
            this.missingSkills = missingSkills;
            this.suggestions = suggestions;
        }

        public int getSemanticScore() {
            return semanticScore;
        }

        public List<String> getMissingSkills() {
            return missingSkills;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }
    }
}