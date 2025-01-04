package org.simbrella.userservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simbrella.userservice.AbstractTestContainer;
import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.requests.RegisterUserRequest;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.dtos.responses.RegisterUserResponse;
import org.simbrella.userservice.models.Role;
import org.simbrella.userservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends AbstractTestContainer {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    RegisterUserRequest existingUser;
    private LoginResponse response;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        existingUser = new RegisterUserRequest();
        existingUser.setFirstName("test");
        existingUser.setLastName("user");
        existingUser.setPassword("Password@1234");

        existingUser.setEmail("test@gmail.com");
        existingUser.setPhoneNumber("08012345678");
        userService.registerUser(existingUser);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(existingUser.getEmail());
        loginRequest.setPassword(existingUser.getPassword());
        response = userService.loginUser(loginRequest);
        objectMapper = new ObjectMapper();
    }


    @AfterEach
    void tearDown() {
        userService.deleteUser(existingUser.getEmail());
    }

    @Test
    void testRegisterUser() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setFirstName("ajay");
        registerUserRequest.setLastName("baba");
        registerUserRequest.setEmail("ajaybaba@email.com");
        registerUserRequest.setUsername("ajaybaba@email.com\"");
        registerUserRequest.setPhoneNumber("08082838283");
        registerUserRequest.setPassword("Password@1234");
        registerUserRequest.setRole(Role.USER);

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(registerUserRequest))
        ).andExpect(status().isCreated()).andDo(print());

        userService.deleteUser(registerUserRequest.getEmail());
    }


    @Test
    void testGetUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/test@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + response.getAccessToken())
        ).andExpect(status().isOk()).andDo(print());
    }

    @Test
    void testLoginUser() throws Exception {
        RegisterUserRequest user = new RegisterUserRequest();
        user.setFirstName("Mohammad");
        user.setLastName("baba");
        user.setPassword("Password@1234");
        user.setEmail("babamu09@gmail.com");
        user.setPhoneNumber("08012345678");
        RegisterUserResponse registerUserResponse = userService.registerUser(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerUserResponse.getEmail());
        loginRequest.setPassword(user.getPassword());
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .content(objectMapper.writeValueAsBytes(loginRequest))
        ).andExpect(status().isOk()).andDo(print());

        userService.deleteUser(user.getEmail());
    }


    @Test
    void testDeleteUser() throws Exception {
        RegisterUserRequest user = new RegisterUserRequest();
        user.setFirstName("Mohammad");
        user.setLastName("baba");
        user.setPassword("Password@1234");
        user.setEmail("babamu09@gmail.com");
        user.setPhoneNumber("08012345678");
        RegisterUserResponse registerUserResponse = userService.registerUser(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerUserResponse.getEmail());
        loginRequest.setPassword(user.getPassword());
        LoginResponse loginResponse = userService.loginUser(loginRequest);

        mockMvc.perform(delete("/api/v1/users/babamu09@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.getAccessToken())
        ).andExpect(status().isNoContent()).andDo(print());
    }
}