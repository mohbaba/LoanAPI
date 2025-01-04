package org.simbrella.loanservice.utils.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanDetails {
        private BigDecimal loanAmount;
        private Integer loanTermMonths;
        private BigDecimal annualIncome;
        private BigDecimal interestRate;
        private BigDecimal monthlyInstallment;
        private BigDecimal totalInterest;


    }
