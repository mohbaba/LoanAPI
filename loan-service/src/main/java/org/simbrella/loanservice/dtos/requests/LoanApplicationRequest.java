package org.simbrella.loanservice.dtos.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplicationRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private BigDecimal loanAmount;
    private Integer loanTermMonths;
    private BigDecimal annualIncome;
}
