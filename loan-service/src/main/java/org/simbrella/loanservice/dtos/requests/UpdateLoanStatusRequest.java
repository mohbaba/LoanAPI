package org.simbrella.loanservice.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.simbrella.loanservice.models.LoanStatus;

@Data
public class UpdateLoanStatusRequest {
    private String loanId;
    private LoanStatus loanStatus;
}
