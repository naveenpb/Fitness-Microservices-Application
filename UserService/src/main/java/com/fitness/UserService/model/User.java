package com.fitness.UserService.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.print.DocFlavor;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    /*By using uuid , we can create a unique id accross the distrubuted systems and its globally unique , dont revel the number of records like auto increment ,Safer in microservices or NoSql Environments ,this is example for uuid 550e8400-e29b-41d4-a716-446655440000 */
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    private String keycloakId;


    @Column(nullable = false)
    private String password;

    private String firstname;
    private String lastname;

    @Enumerated(EnumType.STRING) // tells that we are storing enum values in db
    private UserRole role = UserRole.USER;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
