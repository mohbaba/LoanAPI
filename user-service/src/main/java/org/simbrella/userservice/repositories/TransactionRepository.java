package org.simbrella.userservice.repositories;

import org.simbrella.userservice.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
