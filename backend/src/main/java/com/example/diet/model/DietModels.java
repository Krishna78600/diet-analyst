package com.example.diet.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public final class DietModels {
    private DietModels() {}

    public record DietInput(
            @NotBlank @Size(max = 2000) String foodLog,
            @Min(10) @Max(100) int age,
            @NotBlank String gender,
            @Min(20) @Max(300) int weightKg,
            @Min(100) @Max(250) int heightCm,
            @NotBlank String fitnessGoal) {}

    public record NutritionalBreakdown(
            int calories, int proteinGrams, int carbsGrams, int fatsGrams) {}

    public record DietReport(
            NutritionalBreakdown consumed,
            NutritionalBreakdown idealDailyTarget,
            String deficitOrSurplusStatus,
            List<String> recommendationsToCompleteTarget) {}
}