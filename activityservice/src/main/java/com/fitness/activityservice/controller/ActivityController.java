package com.fitness.activityservice.controller;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request,@RequestHeader("X-User-ID")String userId){
        if(userId != null){
            request.setUserId(userId);
        }
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader("X-User-ID")String userId){
        return ResponseEntity.ok(activityService.getUserActivites(userId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable String id) {
        activityService.deleteActivityById(id);
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getUserActivitiyById(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getUserActivityById(activityId));
    }


//    @PutMapping("/{id}")
//    public ResponseEntity<ActivityResponse> updateActivity(
//            @PathVariable String id,
//            @RequestBody ActivityRequest request,
//            @RequestHeader("X-User-ID") String userId) {
//
//        if (userId != null) {
//            request.setUserId(userId);
//        }
//        return ResponseEntity.ok(activityService.updateActivity(id, request));
//    }

//    Hello , if you are reading this , then try to make update method also , thank you bye



}
