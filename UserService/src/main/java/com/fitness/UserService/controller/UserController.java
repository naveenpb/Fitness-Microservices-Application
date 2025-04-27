package com.fitness.UserService.controller;

import com.fitness.UserService.dto.RegisterRequest;
import com.fitness.UserService.dto.UserResponse;
import com.fitness.UserService.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor //so when we all this annotation , it will create a constructor for all the variables , so there is no need of adding autowired , for any object ,as it will follow constructor based dependency injection
public class UserController {


    private UserService userService;

    @GetMapping("/{userid}")
    //here userrespone is a dto object
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userid ){
        return ResponseEntity.ok(userService.getUserProfile(userid));
        //here we are making call for service layer
    }

    @PostMapping("/register")
    // RegisterRequest is a DTO
    // register is a method in service layer
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }


    @GetMapping("/{userid}/validate")
    //here userrespone is a dto object
    public ResponseEntity<Boolean> validateUser(@PathVariable String userid ){
        return ResponseEntity.ok(userService.existByUserId(userid));
        //here we are making call for service layer
    }
}
