package com.example.diet.controller;

import com.example.diet.model.DietModels.DietInput;
import com.example.diet.model.DietModels.DietReport;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diet")
@CrossOrigin(origins = "*")
public class DietController {

    private static final String SYSTEM_PROMPT = """
            You are an expert clinical dietitian. Analyze the user's unstructured food log text. \
            Deduce the exact macronutrient breakdown consumed. Based on Mifflin-St Jeor equations \
            or standard sports nutrition parameters, calculate their exact daily target for their \
            profile and fitness goals. Compare consumed vs target numbers. Return the output \
            strictly adhering to the requested JSON structure. deficitOrSurplusStatus must be 2-3 \
            sentences. recommendationsToCompleteTarget must contain exactly 3 food items with \
            portion sizes. All numeric fields must be integers.""";

    private final ChatClient chatClient;

    public DietController(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem(SYSTEM_PROMPT).build();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @PostMapping("/analyze")
    public DietReport analyze(@Valid @RequestBody DietInput input) {
        String userMsg = """
                Profile: age %d, gender %s, weight %d kg, height %d cm, goal: %s.
                Food log: %s"""
                .formatted(input.age(), input.gender(), input.weightKg(),
                        input.heightCm(), input.fitnessGoal(), input.foodLog());

        return chatClient.prompt()
                .user(userMsg)
                .call()
                .entity(new ParameterizedTypeReference<DietReport>() {});
    }
}