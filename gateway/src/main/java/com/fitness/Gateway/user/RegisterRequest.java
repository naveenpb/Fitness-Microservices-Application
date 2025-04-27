package com.fitness.Gateway.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is requried")
    @Email(message = "invalid Email format") //these validations are comming from validation dependency added .
    private String email;
    private String keycloakId;
    @NotBlank(message = "password cannot be blank")
    @Size(min = 6, message="password must have altest of 6 characters")
    private String password;
    private String firstname;
    private String lastname;

}
