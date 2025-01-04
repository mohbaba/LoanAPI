package org.simbrella.userservice.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simbrella.userservice.AbstractTestContainer;
import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.exceptions.AuthenticationFailedException;
import org.simbrella.userservice.exceptions.NotFoundException;
import org.simbrella.userservice.exceptions.UserExistsException;
import org.simbrella.userservice.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class AuthServiceImplTest extends AbstractTestContainer {

    @Autowired
    private AuthService authService;
    User newUser;

    @BeforeEach
    void setup() {
        newUser = new User();
        newUser.setFirstName("test");
        newUser.setLastName("user");
        newUser.setPassword("Password@1234");

        newUser.setEmail("test@gmail.com");
        newUser.setPhoneNumber("08012345678");
        authService.createUser(newUser);
    }


    @AfterEach
    void tearDown() {
        authService.deleteUser(newUser.getEmail());
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setFirstName("Mohammad");
        user.setLastName("baba");
        user.setPassword("Password@1234");
        user.setEmail("babamu09@gmail.com");
        user.setPhoneNumber("08012345678");
        authService.createUser(user);

        User savedUser = authService.getUser(user.getEmail());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(savedUser.getLastName()).isEqualTo(user.getLastName());
        authService.deleteUser(user.getEmail());
    }

    @Test
    void testCreateUserWithExistingEmail_ThrowsException(){
        User user = new User();
        user.setFirstName("Mohammad");
        user.setLastName("baba");
        user.setPassword("Password@1234");
        user.setEmail("test@gmail.com");

        user.setPhoneNumber("08012345678");
        assertThrows(UserExistsException.class, ()->authService.createUser(user));
    }

    @Test
    void forgotPassword() {
    }

    @Test
    void assignRole() {
    }

    @Test
    void testGetUser() {
        User user = authService.getUser(newUser.getEmail());
        assertThat(user.getFirstName()).isEqualTo(newUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(newUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(user.getUsername()).isNotNull();
    }

    @Test
    void testGetNonExistingUser_ThrowsException(){
        assertThrows(NotFoundException.class, () -> authService.getUser("nonExistingUser@mail.com"));
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setFirstName("Mohammad");
        user.setLastName("baba");
        user.setPassword("Password@1234");
        user.setEmail("babamu09@gmail.com");
        user.setPhoneNumber("08012345678");
        authService.createUser(user);

        authService.deleteUser(user.getEmail());
        assertThrows(NotFoundException.class, () -> authService.getUser("nonExistingUser@mail.com"));
    }

    @Test
    void testDeleteNonExistingUser(){
        assertThrows(NotFoundException.class, () -> authService.deleteUser("nonExistingUser@mail.com"));

    }

    @Test
    void testLoginUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(newUser.getEmail());
        loginRequest.setPassword(newUser.getPassword());
        LoginResponse loginResponse = authService.loginUser(loginRequest);
        assertThat(loginResponse.getAccessToken()).isNotNull();
        assertThat(loginResponse.getRefreshToken()).isNotNull();
    }

    @Test
    void testLoginUserWithIncorrectDetails(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongNam");
        loginRequest.setPassword("Password@1234");
        assertThrows(AuthenticationFailedException.class, () -> authService.loginUser(loginRequest));
    }
}