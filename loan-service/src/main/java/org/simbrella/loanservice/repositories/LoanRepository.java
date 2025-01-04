package org.simbrella.loanservice.repositories;

import org.simbrella.loanservice.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, String> {
}
