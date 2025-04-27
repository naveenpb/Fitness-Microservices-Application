package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecomendationService {
    public final RecommendationRepository repository;

    public List<Recommendation> getUserRecommendation(String userId) {

        return repository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId) {
        return repository.findByActivityId(activityId)
                .orElseThrow(()-> new RuntimeException(("No recomendation found for this activity")));
    }
}
