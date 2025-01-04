package org.simbrella.loanservice.dtos.requests;

import lombok.Data;
import org.simbrella.loanservice.models.LoanStatus;

@Data
public class UpdateLoanStatusRequest {
    private String loanId;
    private LoanStatus loanStatus;
}
