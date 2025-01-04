package org.simbrella.userservice.dtos.responses;

import lombok.*;
import org.simbrella.userservice.models.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterUserResponse {
    private String id;
    private String firstName;
    private String username;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
}
