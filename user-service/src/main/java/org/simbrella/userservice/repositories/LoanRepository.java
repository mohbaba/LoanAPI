package org.simbrella.userservice.repositories;

import org.simbrella.userservice.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, String> {
}
