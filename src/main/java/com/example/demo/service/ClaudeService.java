package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ClaudeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    public String generateQuestions(String systemPrompt, String userPrompt) {
        return callGeminiWithRetry(systemPrompt, userPrompt, 8192);
    }

    public String evaluateSession(String systemPrompt, String userPrompt, int maxTokens) {
        return callGeminiWithRetry(systemPrompt, userPrompt, maxTokens);
    }

    private String callGeminiWithRetry(String systemPrompt, String userPrompt, int maxTokens) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < 2) {
            try {
                attempts++;
                return executeGeminiCall(systemPrompt, userPrompt, maxTokens);
            } catch (Exception e) {
                lastException = e;
                if (attempts == 1) {
                    try {
                        Thread.sleep(1000); // Wait 1 second before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        throw new RuntimeException("Gemini API call failed after 2 attempts. Error: " + (lastException != null ? lastException.getMessage() : "Unknown"), lastException);
    }

    private String executeGeminiCall(String systemPrompt, String userPrompt, int maxTokens) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build Gemini request structure
        Map<String, Object> requestBody = new HashMap<>();

        // 1. System instruction
        Map<String, Object> systemPart = new HashMap<>();
        systemPart.put("text", systemPrompt);
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(systemPart));
        requestBody.put("systemInstruction", systemInstruction);

        // 2. User content
        Map<String, Object> userPart = new HashMap<>();
        userPart.put("text", userPrompt);
        Map<String, Object> contentItem = new HashMap<>();
        contentItem.put("role", "user");
        contentItem.put("parts", List.of(userPart));
        requestBody.put("contents", List.of(contentItem));

        // 3. Generation config (requesting JSON output)
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.put("maxOutputTokens", maxTokens);
        
        // Disable reasoning/thinking budget to save tokens and prevent truncation
        Map<String, Object> thinkingConfig = new HashMap<>();
        thinkingConfig.put("thinkingBudget", 0);
        generationConfig.put("thinkingConfig", thinkingConfig);
        
        requestBody.put("generationConfig", generationConfig);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GEMINI_URL + geminiApiKey, entity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Received HTTP error from Gemini: " + responseEntity.getStatusCode());
        }

        String body = responseEntity.getBody();
        JsonNode root = objectMapper.readTree(body);
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            String rawText = candidates.get(0).path("content").path("parts").get(0).path("text").asText("");
            return cleanJsonText(rawText);
        }

        throw new RuntimeException("Invalid response shape from Gemini API");
    }

    private String cleanJsonText(String text) {
        if (text == null) return "";
        text = text.trim();
        if (text.startsWith("```")) {
            if (text.startsWith("```json")) {
                text = text.substring(7).trim();
            } else {
                text = text.substring(3).trim();
            }
            if (text.endsWith("```")) {
                text = text.substring(0, text.length() - 3).trim();
            }
        }
        return text;
    }
}
