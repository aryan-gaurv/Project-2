package com.example.authify.io;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileRequest {
    @NotBlank(message = "Name should be not empty")
    private String name;
    @Email(message = "Enter valid email address")
    @NotNull(message = "Email should not be empty")
    private String email;
    @Size(min = 6,message = "Password must be atleast 6 Character")
    private String password;

}
