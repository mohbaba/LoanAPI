package org.simbrella.loanservice.exceptions;

public class NotFoundException extends LoanTechException {
    public NotFoundException(String message) {
        super(message);
    }
}
