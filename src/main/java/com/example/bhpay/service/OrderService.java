package com.example.bhpay.service;

import com.example.bhpay.model.Order;
import com.example.bhpay.model.Product;
import com.example.bhpay.repository.OrderRepository;
import com.example.bhpay.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Nova narudžba sa statusom "NA_CEKANJU"; cijenu i trgovca uzimamo iz baze
    public Order createOrder(Long productId, String customerName, String customerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Proizvod ne postoji: " + productId));
        return orderRepository.save(new Order(product, customerName, customerEmail));
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}