package org.simbrella.userservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.requests.RegisterUserRequest;
import org.simbrella.userservice.dtos.requests.UpdateUserRequest;
import org.simbrella.userservice.dtos.responses.GetUserResponse;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.dtos.responses.RegisterUserResponse;
import org.simbrella.userservice.dtos.responses.UpdateUserResponse;
import org.simbrella.userservice.exceptions.InvalidDetailsException;
import org.simbrella.userservice.exceptions.NotFoundException;
import org.simbrella.userservice.exceptions.UserExistsException;
import org.simbrella.userservice.models.User;
import org.simbrella.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static org.simbrella.userservice.utils.ValidationUtils.*;


@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        validateFields(request.getPhoneNumber(), request.getEmail(), request.getPassword());
        doesUserExists(request.getEmail());
        User user = modelMapper.map(request, User.class);
        authService.createUser(user);
        user = userRepository.save(user);
        return modelMapper.map(user, RegisterUserResponse.class);
    }

    private void validateFields(String phoneNumber, String email, String password) {
        if (!isValidPhoneNumber(phoneNumber))
            throw new InvalidDetailsException("The phone number you entered is not correct");
        if (!isValidEmail(email)) throw new InvalidDetailsException("The email you entered is not correct");
        if (!isValidPassword(password))
            throw new InvalidDetailsException("Password must be between 8 and 16 characters long, including at least one uppercase letter, one lowercase letter, one number, and one special character (e.g., @, #, $, %, ^).");
    }

    private void doesUserExists(String email){
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null)throw new UserExistsException(String.format("User with email: %s already exits", email));
    }


    @Override
    public void deleteUser(String email) {
        User savedUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format("User with email: %s not found", email)));
        authService.deleteUser(email);
        userRepository.delete(savedUser);
    }

    @Override
    public GetUserResponse getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format("User with email: %s not found", email)));
        return modelMapper.map(user, GetUserResponse.class);
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {

        return authService.loginUser(loginRequest);
    }

    @Override
    public UpdateUserResponse updateUser(UpdateUserRequest request) {
        return null;
    }
}
