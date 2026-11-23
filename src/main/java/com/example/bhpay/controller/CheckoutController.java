package com.example.bhpay.controller;

import com.example.bhpay.model.Order;
import com.example.bhpay.model.PaymentTransaction;
import com.example.bhpay.model.Product;
import com.example.bhpay.repository.PaymentTransactionRepository;
import com.example.bhpay.service.OrderService;
import com.example.bhpay.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckoutController {

    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentTransactionRepository transactionRepository;

    public CheckoutController(OrderService orderService,
                              ProductService productService,
                              PaymentTransactionRepository transactionRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }

    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam Long productId, Model model) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "checkout";
    }


    @PostMapping("/process-payment")
    public String processPayment(@RequestParam Long productId,
                                 @RequestParam String customerName,
                                 @RequestParam String customerEmail) {
        Order savedOrder = orderService.createOrder(productId, customerName, customerEmail);
        return "redirect:/gateway/pay/" + savedOrder.getId();
    }

    @GetMapping("/payment-success/{id}")
    public String paymentSuccess(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null || !"PLACENO".equals(order.getStatus())) {
            return "redirect:/gateway/pay/" + id;
        }
        PaymentTransaction tx = transactionRepository.findByOrderId(id).orElse(null);
        model.addAttribute("order", order);
        model.addAttribute("tx", tx);
        return "success";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin-orders";
    }
}