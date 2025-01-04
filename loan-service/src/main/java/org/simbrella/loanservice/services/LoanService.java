package org.simbrella.loanservice.services;

import org.simbrella.loanservice.dtos.User;
import org.simbrella.loanservice.dtos.requests.LoanApplicationRequest;
import org.simbrella.loanservice.dtos.requests.UpdateLoanStatusRequest;
import org.simbrella.loanservice.dtos.responses.LoanApplicationResponse;
import org.simbrella.loanservice.models.Loan;

public interface LoanService {
    LoanApplicationResponse applyForLoan(LoanApplicationRequest request);
    User getUser(String email);
    Loan getLoanDetails(String email);
    Loan updateLoanStatus(UpdateLoanStatusRequest request);
}
