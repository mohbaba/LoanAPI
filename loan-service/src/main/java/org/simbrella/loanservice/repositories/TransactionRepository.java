package org.simbrella.loanservice.repositories;

import org.simbrella.loanservice.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
