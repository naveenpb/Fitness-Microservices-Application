package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("Response From AI : {}", aiResponse);

        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            // Now we need to extract the data from the JSON object and add it to an object of model, so first analysis
            JsonNode analysisJson = mapper.readTree(jsonContent);

            // Now we need to extract the data from the json object and add it to an object of model, so first analysis
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "HeartRate");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "CaloriesBurned");

            /*
             * fullAnalysis – the StringBuilder where the final text is being built.
             *
             * analysisNode – the JSON node that contains different analysis sections.
             *
             * "overall" – the key in the JSON (e.g., analysisNode.get("overall")).
             *
             * "Overall" – a formatted title for display purposes (e.g., used as a heading like === Overall ===).
             */

            // Now extract improvements
            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            // Now extract suggestions
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            // Now extract Safety
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType().name())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType().name())
                .recommendation("Unable to Generate Detailed analysis")
                .improvements(Collections.singletonList("Continue with your daily exercise"))
                .suggestions(Collections.singletonList("Consider Consulting the Prescribed person"))
                .safety(Arrays.asList(
                        "Always Warm Up before Exercise",
                        "Stay hydrated",
                        "Listen to your trainer"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safetyList = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(safety ->{
                safetyList.add(safety.asText());
            });
        }
        return safetyList.isEmpty() ?
                Collections.singletonList("Please Follow General Safety Guidelines") :
                safetyList;
    }

    private List<String> extractSuggestions(JsonNode suggestionNode) {
        List<String> suggestionList = new ArrayList<>();
        if (suggestionNode.isArray()) {
            suggestionNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestionList.add(String.format("%s :%s", workout, description));
            });
        }
        return suggestionList.isEmpty() ?
                Collections.singletonList("No Specific Suggestions provided") :
                suggestionList;
    }

    private List<String> extractImprovements(JsonNode improvementNode) {
        List<String> improvementList = new ArrayList<>();
        if (improvementNode.isArray()) {
            improvementNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvementList.add(String.format("%s :%s", area, detail));
            });
        }
        return improvementList.isEmpty() ?
                Collections.singletonList("No Specific improvements provided") :
                improvementList;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        // Check if the key exists in the analysisNode, if not there simply StringBuilder will skip adding this particular node
        if (!analysisNode.path(key).isMissingNode()) {
            // Add the label (prefix) to the StringBuilder
            fullAnalysis.append(prefix)
                    // Add the value of the key from the JSON node (converted to text)
                    .append(analysisNode.path(key).asText())
                    // Add two newlines for spacing between sections
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                Analyze this fitness activity and provide detailed recommendations in the following json format:
                {
                  "analysis": {
                    "overall": "Overall analysis here",
                    "pace": "Pace analysis here",
                    "heartRate": "Heart rate analysis here",
                    "caloriesBurned": "Calories analysis here"
                  },
                  "improvements": [
                    {
                      "area": "Area name",
                      "recommendation": "Detailed recommendation"
                    }
                  ],
                  "suggestions": [
                    {
                      "workout": "Workout name",
                      "description": "Detailed workout description"
                    }
                  ],
                  "safety": [
                    "Safety point 1",
                    "Safety point 2"
                  ]
                }
                Analyze this activity:
                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s
                
                Provide detailed analysis focusing on performance, improvements, suggestions, next workout suggestions and safety guidelines.
                Ensure the response follows the EXACT JSON format shown above:
                """,
                activity.getType().name(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics() != null ? activity.getAdditionalMetrics().toString() : "No additional metrics"
        );
    }
}
