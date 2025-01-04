package org.simbrella.userservice.services;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.simbrella.userservice.dtos.requests.LoginRequest;
import org.simbrella.userservice.dtos.responses.LoginResponse;
import org.simbrella.userservice.exceptions.AuthenticationFailedException;
import org.simbrella.userservice.exceptions.InvalidDetailsException;
import org.simbrella.userservice.exceptions.NotFoundException;
import org.simbrella.userservice.exceptions.UserExistsException;
import org.simbrella.userservice.models.Role;
import org.simbrella.userservice.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.simbrella.userservice.utils.ValidationUtils.*;


@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final Keycloak keycloak;
    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin.clientId}")
    private String client;

    @Value("${app.keycloak.admin.clientSecret}")
    private String secret;

    @Value("${app.keycloak.token.uri}")
    private String tokenUri;

    public AuthServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public Response createUser(User authUser) {
        validateFields(authUser.getPhoneNumber(), authUser.getEmail(), authUser.getPassword());
        UserRepresentation userRepresentation = mapFrom(authUser);
        List<CredentialRepresentation> credentials = getCredentialRepresentations(authUser);
        userRepresentation.setCredentials(credentials);
        UsersResource usersResource = getUsersResource();
        Response response = usersResource.create(userRepresentation);


        try {
            String userId = usersResource.search(authUser.getEmail()).get(0).getId();
//            assignRole(userRepresentation.getEmail(), authUser.getRole());
//            sendVerificationEmail(userId);
        } catch (Exception e) {
            throw new UserExistsException(e.getMessage());
        }
        return response;

    }

    private void validateFields(String phoneNumber, String email, String password) {
        if (!isValidPhoneNumber(phoneNumber))
            throw new InvalidDetailsException("The phone number you entered is not correct");
        if (!isValidEmail(email)) throw new InvalidDetailsException("The email you entered is not correct");
        if (!isValidPassword(password))
            throw new InvalidDetailsException("Password must be between 8 and 16 characters long, including at least one uppercase letter, one lowercase letter, one number, and one special character (e.g., @, #, $, %, ^).");
    }

    private void doesUserExist(String email){

    }
    private static UserRepresentation mapFrom(User authUser) {

        ModelMapper modelMapper = new ModelMapper();
        UserRepresentation userRepresentation = modelMapper.map(authUser, UserRepresentation.class);
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(authUser.getEmail());
        return userRepresentation;
    }

    private static List<CredentialRepresentation> getCredentialRepresentations(User authUser) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(authUser.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        List<CredentialRepresentation> credentials = new ArrayList<>();
        credentials.add(credentialRepresentation);
        return credentials;
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    @Override
    public void forgotPassword(String email) {
        UsersResource usersResource = getUsersResource();
        UserRepresentation userRepresentations = usersResource.searchByEmail(email, true).getFirst();
        UserResource userResource = usersResource.get(userRepresentations.getId());
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));


    }

    @Override
    public void assignRole(String email, Role role) {
        UserResource userResource = getUserResource(email);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation roleRepresentation = rolesResource.get(String.valueOf(role)).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    @Override
    public User getUser(String email) {
        try {
            UsersResource usersResource = getUsersResource();
            String id = usersResource.search(email).getFirst().getId();
            UserRepresentation representation = usersResource.get(id).toRepresentation();
            return User.builder()
                    .firstName(representation.getFirstName())
                    .lastName(representation.getLastName())
                    .username(representation.getUsername())
                    .email(representation.getEmail())
                    .build();
        } catch (NoSuchElementException e) {
            throw new NotFoundException(String.format("User with email: %s not found", email));
        }
    }



    @Override
    public void deleteUser(String email) {
        try {
            UsersResource usersResource = getUsersResource();
            String userId = usersResource.search(email).getFirst().getId();
            usersResource.delete(userId);
        } catch (NoSuchElementException e) {
            throw new NotFoundException(String.format("Cannot delete non existing user: ", email));

        }

    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        RestTemplate template = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", client);
        params.add("client_secret", secret);
        params.add("username", loginRequest.getUsername());
        params.add("password", loginRequest.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<LoginResponse> response = template.postForEntity(tokenUri, request, LoginResponse.class);
            log.info("Response body: " + response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new AuthenticationFailedException("Authentication failed: " + ex.getMessage());
        }
    }

    private UserResource getUserResource(String email) {
        UsersResource usersResource = getUsersResource();
        String id = usersResource.search(email).getFirst().getId();
        return usersResource.get(id);
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }
}
