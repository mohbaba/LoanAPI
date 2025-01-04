package org.simbrella.loanservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.simbrella.loanservice.models.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationResponse {
    private BigDecimal amount;
    private Float interestRate;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private LoanStatus loanStatus;
    private String userId;
}
