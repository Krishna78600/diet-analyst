package com.example.diet.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.diet.model.DietModels.DietInput;
import com.example.diet.model.DietModels.DietReport;
import com.example.diet.model.DietModels.RecipeInput;
import com.example.diet.model.DietModels.RecipeReport;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/diet")
@CrossOrigin(origins = "*")
public class DietController {

        private static final String SYSTEM_PROMPT = """
            You are an Indian clinical dietitian. The food log may use Hindi, Hinglish or English \
            names (roti, dal, chawal, sabzi, paneer, poha, chai, etc.). If quantities are missing, \
            assume typical Indian home portions (1 roti, 1 katori dal or sabzi, 1 plate rice).
            Steps: 1) Estimate the total calories, protein, carbs and fats eaten. \
            2) Calculate the daily target with Mifflin-St Jeor, adjusted for the fitness goal. \
            3) Compare eaten vs target.
            Rules:
            - deficitOrSurplusStatus: 2-3 short sentences in simple English, plain words, \
            no medical jargon. Say what is low or high and by how much.
                       - recommendationsToCompleteTarget: exactly 3 items, STRICTLY VEGETARIAN. NEVER suggest \
            chicken, fish, meat, egg or any non-veg item, even for protein. Use only foods like paneer, \
            dal, rajma, chana, soya chunks, tofu, curd, milk, sprouts, nuts, seeds, oats, roti, rice. \
            Each needs a portion size and should target the biggest gap.
            - All numbers are integers. If the log has no food, set consumed to 0.""";
    
    
    // RECIPE PROMPT
               private static final String RECIPE_PROMPT = """
            You are an Indian home-cooking chef. The user lists ingredients in Hindi, Hinglish or \
            English, often just vegetable names. Suggest exactly 3 STRICTLY VEGETARIAN dishes (no \
            meat, fish or egg) built around the listed ingredients. Assume the kitchen always has \
            salt, water, turmeric, red chili powder, cumin, coriander powder, garam masala, \
            ginger, garlic and sugar, so never ask for these as missing items. Do not require \
            any other main ingredient the user did not list; if the list is short, make simple \
            dishes (sabzi, bhurta, curry, stir-fry, soup).
            Cooking mode is given in the user message. WITH OIL: normal Indian cooking. \
            NO OIL: use zero oil, ghee or butter; cook by boiling, steaming, pressure cooking, \
            dry roasting, baking or sauteing in water, and never list oil in ingredients or steps.
            For each dish give: name, cookingTime (e.g. "20 min"), ingredientsNeeded with \
            quantities for the given servings, steps (4-6 short, simple steps), \
            caloriesPerServing and proteinGrams as integers. tip: one short line. \
            Use simple language.""";

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
                Food log: %s
                Recommend vegetarian foods only (no meat, fish or egg)."""
                .formatted(input.age(), input.gender(), input.weightKg(),
                        input.heightCm(), input.fitnessGoal(), input.foodLog());

        return chatClient.prompt()
                .user(userMsg)
                .call()
                .entity(new ParameterizedTypeReference<DietReport>() {});
    }

    @PostMapping("/recipes")
    public RecipeReport recipes(@Valid @RequestBody RecipeInput input) {
                String userMsg = "Ingredients available: %s. Servings: %d. Cooking mode: %s."
                .formatted(input.ingredients(), input.servings(),
                        input.useOil() ? "WITH OIL" : "NO OIL (no oil, ghee or butter)");
        return chatClient.prompt()
                .system(RECIPE_PROMPT)
                .user(userMsg)
                .call()
                .entity(new ParameterizedTypeReference<RecipeReport>() {});
    }
}