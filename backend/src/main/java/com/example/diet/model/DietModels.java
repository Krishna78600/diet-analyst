package com.example.diet.model;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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


    public record RecipeInput(
            @NotBlank @Size(max = 1000) String ingredients,
            @Min(1) @Max(10) int servings,
            boolean useOil) {}

    public record Recipe(
            String name, String cookingTime, List<String> ingredientsNeeded,
            List<String> steps, int caloriesPerServing, int proteinGrams) {}

    public record RecipeReport(List<Recipe> recipes, String tip) {}
}