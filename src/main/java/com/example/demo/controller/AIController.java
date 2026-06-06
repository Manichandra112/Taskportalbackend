package com.example.demo.controller;

import com.example.demo.dto.AIGenerateRequest;
import com.example.demo.dto.AIGenerateResponse;
import com.example.demo.service.AIService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public ResponseEntity<AIGenerateResponse> generateTaskDetails(
            @Valid @RequestBody AIGenerateRequest request) {
        AIGenerateResponse response = aiService.generateTaskDetails(request.getTitle());
        return ResponseEntity.ok(response);
    }
}
