package com.example.bhpay.service;




import com.example.bhpay.model.Order;
import com.example.bhpay.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Snimanje nove narudžbe sa statusom "NA_CEKANJU"
    public Order createOrder(String productName, double amount, String customerName, String customerEmail) {
        Order order = new Order(productName, amount, customerName, customerEmail, "NA_CEKANJU");
        return orderRepository.save(order);
    }

    // Pronađi narudžbu po ID-ju
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // Ažuriraj status narudžbe (npr. kada plaćanje uspije)
    public void updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }

    // Lista svih narudžbi (za admin pregled)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}