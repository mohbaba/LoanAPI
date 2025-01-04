package org.simbrella.userservice.exceptions;

public class AuthenticationFailedException extends LoanTechException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
