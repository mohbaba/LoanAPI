package org.simbrella.userservice.services;


import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.requests.RegisterUserRequest;
import org.simbrella.userservice.dtos.requests.UpdateUserRequest;
import org.simbrella.userservice.dtos.responses.GetUserResponse;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.dtos.responses.RegisterUserResponse;
import org.simbrella.userservice.dtos.responses.UpdateUserResponse;

public interface UserService {
    RegisterUserResponse registerUser(RegisterUserRequest request);
    void deleteUser(String email);
    GetUserResponse getUser(String email);
    LoginResponse loginUser(LoginRequest loginRequest);
    UpdateUserResponse updateUser(UpdateUserRequest request);

}
