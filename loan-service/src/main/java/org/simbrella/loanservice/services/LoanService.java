package org.simbrella.loanservice.services;

import org.simbrella.loanservice.dtos.User;
import org.simbrella.loanservice.dtos.requests.LoanApplicationRequest;
import org.simbrella.loanservice.dtos.responses.LoanApplicationResponse;

public interface LoanService {
    LoanApplicationResponse applyForLoan(LoanApplicationRequest request);

    User getUser(String email);
}
