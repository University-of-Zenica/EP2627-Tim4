package com.example.bhpay.repository;

import com.example.bhpay.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByJib(String jib);
    boolean existsByJib(String jib);
    boolean existsByIban(String iban);
    List<Merchant> findByActiveTrue();
}