package com.example.bhpay.repository;

import com.example.bhpay.model.CardDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardDetailsRepository extends JpaRepository<CardDetails, Long> {
    Optional<CardDetails> findByPaymentToken(String paymentToken);
}