package org.simbrella.userservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.requests.RegisterUserRequest;
import org.simbrella.userservice.dtos.requests.UpdateUserRequest;
import org.simbrella.userservice.dtos.responses.GetUserResponse;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.dtos.responses.RegisterUserResponse;
import org.simbrella.userservice.dtos.responses.UpdateUserResponse;
import org.simbrella.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{email}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable("email") String email) {
        GetUserResponse response = userService.getUser(email);
        return ResponseEntity.ok(response);
    }

//    @PutMapping("/{email}")
//    public ResponseEntity<UpdateUserResponse> updateUser(
//            @PathVariable String email,
//            @Valid @RequestBody UpdateUserRequest request) {
//        // Assuming the request contains the email in its payload as well
////        request.setEmail(email);
////        UpdateUserResponse response = userService.updateUser(request);
//        return ResponseEntity.ok();
//    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
}
