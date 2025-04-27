package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j //for using log , its a simple logging facade for java
public class ActivityService {

    private final ActivityRepository repository; // as we have used requriedargsconstructor here we use final ,because requried args constructor will create constructor only for the final and non null variables , just an another way of dependency Injection...

    private final UserValidationService validation;
//    this is the class , helps us synchroze rabbitmq messages
    private final RabbitTemplate rabbittemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {


        Boolean isValidUser = validation.validateUser(request.getUserId());


        if(!isValidUser){
            throw new RuntimeException("Invalid User: "+request.getUserId());
        }//so we are using this because , if there is no user with certain id , then it dosent makes sense to add those activity in activity service



        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAddittionalMetrics())
                .build(); // another way to convert dto to entiry object.

        Activity savedActivity = repository.save(activity);

        //publish to rabbitmq queue , for ai processing

        try {

            rabbittemplate.convertAndSend(exchange,routingKey,savedActivity);

        }catch (Exception e){
                log.error("failed to publish activity to RabbitMQ" ,e);
        }




        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAddittionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;

    }

    public List<ActivityResponse> getUserActivites(String userId) {
        List<Activity> activities = repository.findByUserId(userId);
        //now we need to map every element of list to dto object , so we use streams to do sp
        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public ActivityResponse getUserActivityById(String activityId) {
        return repository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Activity Not Found with id" + activityId));

    }

    public void deleteActivityById(String id) {
        repository.deleteById(id);
    }

//    public ActivityResponse updateActivity(String id, ActivityRequest request) {
//        // validate user
//        Boolean isValidUser = validation.validateUser(request.getUserId());
//        if (!isValidUser) {
//            throw new RuntimeException("Invalid User: " + request.getUserId());
//        }
//
//        // fetch existing activity
//        Activity existing = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Activity Not Found with id " + id));
//
//        // (optional) ensure the activity belongs to the same user
//        if (!existing.getUserId().equals(request.getUserId())) {
//            throw new RuntimeException("User ID mismatch for activity " + id);
//        }
//
//        // update fields
//        existing.setType(request.getType());
//        existing.setDuration(request.getDuration());
//        existing.setCaloriesBurned(request.getCaloriesBurned());
//        existing.setStartTime(request.getStartTime());
//        existing.setAdditionalMetrics(request.getAddittionalMetrics());
//
//        // save & publish update
//        Activity updated = repository.save(existing);
//        try {
//            rabbittemplate.convertAndSend(exchange, routingKey, updated);
//        } catch (Exception e) {
//            log.error("Failed to publish updated activity to RabbitMQ", e);
//        }
//
//        // return DTO
//        return mapToResponse(updated);
//    }

//    If you are reading this , part of code , then please uncomment this put method , and also remember to uncomment the update part of code in ActivityController ,
//    By Uncommenting , you can test these api endpoints , in postman , But for me while integrating forntend it was lot of work to handle this updatefeature , So i have commented this part ,  for sure I will try to add this feature in future ,
//    if you are able to achive this with frontend , then dont forget to push the branch ....Thank you
}
