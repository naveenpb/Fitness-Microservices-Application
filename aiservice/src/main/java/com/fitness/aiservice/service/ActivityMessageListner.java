package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListner {

    public final ActivityAIService aiservice;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue" )
    public void processActivity(Activity activity){
        log.info("Recived activity for processing: {}" ,activity.getId());
        //log.info("Genrated Recomendations :{}" , "TEST");
        Recommendation recommendation = aiservice.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }

}
