package com.example.Wallet.repository;

import com.example.Wallet.entities.Transaction;
import com.example.Wallet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t where t.sender = ?1 or t.receiver = ?1")
    public List<Transaction> findTransactionsOfUser(User user);

    @Query("SELECT t FROM Transaction t where (t.sender = ?1 or t.receiver = ?1) and (t.timestamp BETWEEN ?2 AND ?3)")
    public List<Transaction> findTransactionsOfUserDateBased(User user, LocalDateTime startDate, LocalDateTime endDate);
}
