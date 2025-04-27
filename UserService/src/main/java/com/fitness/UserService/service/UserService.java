package com.fitness.UserService.service;

import com.fitness.UserService.dto.RegisterRequest;
import com.fitness.UserService.dto.UserResponse;
import com.fitness.UserService.model.User;
import com.fitness.UserService.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserResponse getUserProfile(String userid) {
        User user = repository.findById(userid)
                    .orElseThrow(()->new RuntimeException("user Not fond"));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail((user.getEmail()));
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;

    }





    public UserResponse register(@Valid RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())){
            UserResponse existinguser= repository.findByEmail(request.getEmail());
            UserResponse userResponse = new UserResponse();
            userResponse.setId(existinguser.getId());
            userResponse.setKeycloakId(existinguser.getKeycloakId());
            userResponse.setPassword(existinguser.getPassword());
            userResponse.setEmail((existinguser.getEmail()));
            userResponse.setFirstname(existinguser.getFirstname());
            userResponse.setLastname(existinguser.getLastname());
            userResponse.setCreatedAt(existinguser.getCreatedAt());
            userResponse.setUpdatedAt(existinguser.getUpdatedAt());

            return userResponse;

        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setKeycloakId(request.getKeycloakId());


        User saveduser = repository.save(user);
        //now we need to return a respone object , which should be an dto object
        UserResponse userResponse = new UserResponse();
        userResponse.setId(saveduser.getId());
        userResponse.setKeycloakId(saveduser.getKeycloakId());
        userResponse.setPassword(saveduser.getPassword());
        userResponse.setEmail((saveduser.getEmail()));
        userResponse.setFirstname(saveduser.getFirstname());
        userResponse.setLastname(saveduser.getLastname());
        userResponse.setCreatedAt(saveduser.getCreatedAt());
        userResponse.setUpdatedAt(saveduser.getUpdatedAt());

        return userResponse;



    }

    public Boolean existByUserId(String userid) {
        return repository.existsByKeycloakId(userid);
    }
}
