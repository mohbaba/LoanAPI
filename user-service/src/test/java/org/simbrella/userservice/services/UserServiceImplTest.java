package org.simbrella.userservice.services;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simbrella.userservice.AbstractTestContainer;
import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.requests.RegisterUserRequest;
import org.simbrella.userservice.dtos.responses.GetUserResponse;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.dtos.responses.RegisterUserResponse;
import org.simbrella.userservice.exceptions.AuthenticationFailedException;
import org.simbrella.userservice.exceptions.InvalidDetailsException;
import org.simbrella.userservice.exceptions.NotFoundException;
import org.simbrella.userservice.exceptions.UserExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class UserServiceImplTest extends AbstractTestContainer {
    @Autowired
    private UserService userService;
    RegisterUserRequest existingUser;

    @BeforeEach
    void setup() {
        existingUser = new RegisterUserRequest();
        existingUser.setFirstName("test");
        existingUser.setLastName("user");
        existingUser.setPassword("Password@1234");

        existingUser.setEmail("test@gmail.com");
        existingUser.setPhoneNumber("08012345678");
        userService.registerUser(existingUser);
    }


    @AfterEach
    void tearDown() {
        userService.deleteUser(existingUser.getEmail());
    }

    @Test
    void testRegisterUser() {
        RegisterUserRequest user = getRegisterUserRequest();
        RegisterUserResponse registerUserResponse = userService.registerUser(user);

        assertThat(registerUserResponse.getEmail()).isEqualTo(user.getEmail());
        assertThat(registerUserResponse.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(registerUserResponse.getLastName()).isEqualTo(user.getLastName());
        assertThat(registerUserResponse.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        userService.deleteUser(user.getEmail());
    }
    @Test
    void testRegisterUserWithExistingEmail() {

        assertThrows(UserExistsException.class, () -> userService.registerUser(existingUser));
    }
    @Test
    void testRegisterUserWithIncorrectPhoneNumber() {
        RegisterUserRequest user = getRegisterUserRequest();
        user.setPhoneNumber("0000000");
        assertThrows(InvalidDetailsException.class, () -> userService.registerUser(user));
    }
    @Test
    void testRegisterUserWithIncorrectEmail() {
        RegisterUserRequest user = getRegisterUserRequest();
        user.setEmail("badEmail.com");
        assertThrows(InvalidDetailsException.class, () -> userService.registerUser(user));
    }
    @Test
    void testRegisterUserWithBadPassword() {
        RegisterUserRequest user = getRegisterUserRequest();
        user.setPassword("badpassword");
        assertThrows(InvalidDetailsException.class, () -> userService.registerUser(user));
    }

    @NotNull
    private static RegisterUserRequest getRegisterUserRequest() {
        RegisterUserRequest user = new RegisterUserRequest();
        user.setFirstName("Mohammad");
        user.setLastName("baba");
        user.setPassword("Password@1234");
        user.setEmail("babamu09@gmail.com");
        user.setPhoneNumber("08012345678");
        return user;
    }

    @Test
    void testDeleteUser() {
        RegisterUserRequest user = getRegisterUserRequest();
        RegisterUserResponse registerUserResponse = userService.registerUser(user);
        userService.deleteUser(registerUserResponse.getEmail());
        assertThrows(NotFoundException.class, ()-> userService.deleteUser(registerUserResponse.getEmail()));
    }

    @Test
    void getUser() {
        GetUserResponse user = userService.getUser(existingUser.getEmail());

        assertThat(user.getEmail()).isEqualTo(existingUser.getEmail());
        assertThat(user.getFirstName()).isEqualTo(existingUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(existingUser.getLastName());
        assertThat(user.getPhoneNumber()).isEqualTo(existingUser.getPhoneNumber());
        assertThat(user.getId()).isNotNull();
    }
    @Test
    void getNonExistingUserTest() {

        assertThrows(NotFoundException.class, () -> userService.getUser("wrongemail"));
    }

    @Test
    void testLoginUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(existingUser.getEmail());
        loginRequest.setPassword(existingUser.getPassword());
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        assertThat(loginResponse.getAccessToken()).isNotNull();
        assertThat(loginResponse.getRefreshToken()).isNotNull();
    }

    @Test
    void testLoginUserWithIncorrectDetails(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongNam");
        loginRequest.setPassword("Password@1234");
        assertThrows(AuthenticationFailedException.class, () -> userService.loginUser(loginRequest));
    }
    @Test
    void updateUser() {

    }
}