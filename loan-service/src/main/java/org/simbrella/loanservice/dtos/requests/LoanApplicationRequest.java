package org.simbrella.loanservice.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanApplicationRequest {

    @NotBlank(message = "Full name is required.")
    @Size(max = 100, message = "Full name must not exceed 100 characters.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    private String phoneNumber;

    @NotBlank(message = "Address is required.")
    @Size(max = 200, message = "Address must not exceed 200 characters.")
    private String address;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @NotNull(message = "Loan amount is required.")
    @DecimalMin(value = "1000.00", message = "Loan amount must be at least 1000.00.")
    @Digits(integer = 15, fraction = 2, message = "Loan amount must be a valid decimal number.")
    private BigDecimal loanAmount;

    @NotNull(message = "Loan term is required.")
    @Min(value = 1, message = "Loan term must be at least 1 month.")
    @Max(value = 12, message = "Loan term must not exceed 12 months.")
    private Integer loanTermMonths;

    @NotNull(message = "Annual income is required.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Annual income must be greater than zero.")
    @Digits(integer = 15, fraction = 2, message = "Annual income must be a valid decimal number.")
    private BigDecimal annualIncome;

}

