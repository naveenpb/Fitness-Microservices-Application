package com.fitness.UserService.repository;

import com.fitness.UserService.dto.UserResponse;
import com.fitness.UserService.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
//user is model , and String is the primary id datatype
public interface UserRepository extends JpaRepository<User,String> {
    boolean existsByEmail(@NotBlank(message = "Email is requried") @Email(message = "invalid Email format") String email);

    Boolean existsByKeycloakId(String userid);

    UserResponse findByEmail(@NotBlank(message = "Email is requried") @Email(message = "invalid Email format") String email);
}
