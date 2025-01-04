package org.simbrella.loanservice.dtos;

import lombok.Data;

@Data
public class User {
    private String id;
    private String firstName;
    private String username;
    private String lastName;
    private String email;
    private String phoneNumber;
}
