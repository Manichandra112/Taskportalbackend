package com.example.demo.service;

import com.example.demo.dto.AIGenerateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Generate task description, priority, and estimated time using Google Gemini AI.
     * Falls back to default values if the API is unavailable or key is missing.
     */
    public AIGenerateResponse generateTaskDetails(String taskTitle) {
        // If no API key is configured, return fallback
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            log.warn("Gemini API key not configured. Returning fallback response.");
            return getFallbackResponse(taskTitle);
        }

        try {
            String prompt = buildPrompt(taskTitle);
            String requestBody = buildRequestBody(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(geminiApiUrl + "?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseGeminiResponse(response.body());
            } else {
                log.error("Gemini API error. Status: {}, Body: {}", response.statusCode(), response.body());
                return getFallbackResponse(taskTitle);
            }

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            return getFallbackResponse(taskTitle);
        }
    }

    /**
     * Build a structured prompt for the Gemini AI
     */
    private String buildPrompt(String taskTitle) {
        return String.format(
                "You are a project management assistant. Given a task title, generate the following in JSON format only (no markdown, no code blocks):\n" +
                "1. \"description\": A clear, actionable task description (2-3 sentences)\n" +
                "2. \"priority\": One of LOW, MEDIUM, HIGH, CRITICAL\n" +
                "3. \"estimatedTime\": Estimated completion time (e.g., '2 hours', '1 day')\n\n" +
                "Task Title: \"%s\"\n\n" +
                "Respond with ONLY valid JSON, no other text.", taskTitle);
    }

    /**
     * Build Gemini API request body
     */
    private String buildRequestBody(String prompt) throws Exception {
        String escapedPrompt = objectMapper.writeValueAsString(prompt);
        return String.format(
                "{\"contents\":[{\"parts\":[{\"text\":%s}]}]}", escapedPrompt);
    }

    /**
     * Parse the Gemini API response and extract generated content
     */
    private AIGenerateResponse parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String generatedText = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();

            // Clean up the response (remove markdown code blocks if present)
            generatedText = generatedText.trim();
            if (generatedText.startsWith("```json")) {
                generatedText = generatedText.substring(7);
            }
            if (generatedText.startsWith("```")) {
                generatedText = generatedText.substring(3);
            }
            if (generatedText.endsWith("```")) {
                generatedText = generatedText.substring(0, generatedText.length() - 3);
            }
            generatedText = generatedText.trim();

            JsonNode aiOutput = objectMapper.readTree(generatedText);

            return AIGenerateResponse.builder()
                    .description(aiOutput.path("description").asText("No description generated"))
                    .priority(aiOutput.path("priority").asText("MEDIUM"))
                    .estimatedTime(aiOutput.path("estimatedTime").asText("Unknown"))
                    .aiGenerated(true)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            return getFallbackResponse("task");
        }
    }

    /**
     * Provide a fallback response when AI service is unavailable
     */
    private AIGenerateResponse getFallbackResponse(String taskTitle) {
        String description = String.format(
                "Complete the task: %s. Break down the task into smaller steps, " +
                "identify dependencies, and set clear deliverables.", taskTitle);

        return AIGenerateResponse.builder()
                .description(description)
                .priority("MEDIUM")
                .estimatedTime("2 hours")
                .aiGenerated(false)
                .build();
    }
}
