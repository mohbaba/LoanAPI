package org.simbrella.loanservice.controllers;

import lombok.RequiredArgsConstructor;
import org.simbrella.loanservice.dtos.requests.LoanApplicationRequest;
import org.simbrella.loanservice.dtos.requests.UpdateLoanStatusRequest;
import org.simbrella.loanservice.dtos.responses.LoanApplicationResponse;
import org.simbrella.loanservice.exceptions.InvalidDetailsException;
import org.simbrella.loanservice.exceptions.LoanTechException;
import org.simbrella.loanservice.exceptions.NotFoundException;
import org.simbrella.loanservice.models.Loan;
import org.simbrella.loanservice.services.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanApplicationResponse> applyForLoan(@RequestBody LoanApplicationRequest request) {
        try {
            LoanApplicationResponse response = loanService.applyForLoan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidDetailsException e) {
            return ResponseEntity.badRequest().body(null); // Return 400 Bad Request
        } catch (LoanTechException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<Loan> getLoanDetails(@PathVariable String email) {
        try {
            Loan loan = loanService.getLoanDetails(email);
            return ResponseEntity.ok(loan);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 Not Found
        } catch (LoanTechException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 Internal Server Error
        }
    }

    @PutMapping("/status")
    public ResponseEntity<Loan> updateLoanStatus(@RequestBody UpdateLoanStatusRequest request) {
        try {
            Loan updatedLoan = loanService.updateLoanStatus(request);
            return ResponseEntity.ok(updatedLoan);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 Not Found
        } catch (LoanTechException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 Internal Server Error
        }
    }
}