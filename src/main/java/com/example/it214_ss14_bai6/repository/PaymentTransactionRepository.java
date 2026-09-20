package com.example.it214_ss14_bai6.repository;

import com.example.it214_ss14_bai6.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);
}
