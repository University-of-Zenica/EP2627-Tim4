package com.example.bhpay.repository;

import com.example.bhpay.model.PaymentTransaction;
import com.example.bhpay.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionReference(String transactionReference);
    Optional<PaymentTransaction> findByOrderId(Long orderId);
    List<PaymentTransaction> findByStatus(TransactionStatus status);
}