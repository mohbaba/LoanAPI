package org.simbrella.loanservice.repositories;

import org.simbrella.loanservice.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, String> {
    Optional<Loan> findByUserId(String userId);
}
