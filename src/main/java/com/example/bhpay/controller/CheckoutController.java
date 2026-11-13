package com.example.bhpay.controller;

import com.example.bhpay.model.Order;
import com.example.bhpay.model.Product; // Obavezno uvezi Product model
import com.example.bhpay.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays; // Za listu proizvoda
import java.util.List;

@Controller
public class CheckoutController {

    private final OrderService orderService;

    public CheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. POČETNA STRANICA SA PROIZVODIMA
    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = Arrays.asList(
                new Product(1L, "Domaća Bosanska Kafa (500g)", 12.50),
                new Product(2L, "Planinski Med (1kg)", 25.00),
                new Product(3L, "Domaći Čaj od Nane (100g)", 5.00)
        );

        model.addAttribute("products", products);
        return "index"; // Traži index.html
    }

    // 2. PRIKAZ FORME ZA UNOS PODATAKA
    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam String productName, @RequestParam double price, Model model) {
        model.addAttribute("productName", productName);
        model.addAttribute("price", price);
        return "checkout";
    }

    // 3. OBRADA UNESENIH PODATAKA I KREIRANJE NARUDŽBE
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam String productName,
                                 @RequestParam double price,
                                 @RequestParam String customerName,
                                 @RequestParam String customerEmail) {

        Order savedOrder = orderService.createOrder(productName, price, customerName, customerEmail);
        return "redirect:/payment-success/" + savedOrder.getId();
    }

    // 4. USPJEŠNO PLAĆANJE
    @GetMapping("/payment-success/{id}")
    public String paymentSuccess(@PathVariable Long id, Model model) {
        orderService.updateOrderStatus(id, "PLACENO");

        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "success";
    }

    // 5. ADMIN PREGLED SVIH NARUDŽBI
    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin-orders";
    }
}