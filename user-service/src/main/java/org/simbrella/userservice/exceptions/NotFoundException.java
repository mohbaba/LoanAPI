package org.simbrella.userservice.exceptions;

public class NotFoundException extends LoanTechException {
    public NotFoundException(String message) {
        super(message);
    }
}
