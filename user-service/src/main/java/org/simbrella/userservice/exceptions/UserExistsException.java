package org.simbrella.userservice.exceptions;

public class UserExistsException extends LoanTechException{
    public UserExistsException(String message) {
        super(message);
    }
}
