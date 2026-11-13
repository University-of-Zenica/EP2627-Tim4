package com.example.bhpay.repository;



import com.example.bhpay.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Spring Data JPA automatski obezbjeđuje metode poput save(), findAll(), findById() itd.
}
