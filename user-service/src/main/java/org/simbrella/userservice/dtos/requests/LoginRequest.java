package org.simbrella.userservice.dtos.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @NotEmpty(message = "Username must not be empty")
    private String username;

    @NotBlank(message = "Password is required")
    @NotEmpty(message = "Password must not be empty")
    private String password;
}
