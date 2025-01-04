package org.simbrella.userservice.services;

import jakarta.ws.rs.core.Response;
import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.models.Role;
import org.simbrella.userservice.models.User;

public interface AuthService {
    Response createUser(User authUser);

    void forgotPassword(String email);

    void assignRole(String email, Role role);

    User getUser(String email);


    void deleteUser(String email);

    LoginResponse loginUser(LoginRequest loginRequest);

}
