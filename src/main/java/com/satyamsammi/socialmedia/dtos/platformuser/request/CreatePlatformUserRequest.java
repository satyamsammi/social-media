package com.satyamsammi.socialmedia.dtos.platformuser.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@SuperBuilder()
public class CreatePlatformUserRequest {
    @NotBlank(message = "Username should be unique and mandatory")
    private String username;

    @NotBlank(message = "FirstName is mandatory")
    private String firstName;

    @NotBlank(message = "LastName is mandatory")
    private String LastName;

    @NotBlank(message = "Email is mandatory")
    private String email;

}
