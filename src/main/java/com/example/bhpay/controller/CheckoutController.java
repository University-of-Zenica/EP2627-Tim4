package com.example.bhpay.controller;

import com.example.bhpay.model.Order;
import com.example.bhpay.model.Product;
import com.example.bhpay.service.OrderService;
import com.example.bhpay.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckoutController {

    private final OrderService orderService;
    private final ProductService productService;

    public CheckoutController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    // 1. POČETNA STRANICA - proizvodi iz baze
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }

    // 2. FORMA ZA UNOS PODATAKA - šalje se samo productId, cijena se čita iz baze
    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam Long productId, Model model) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "checkout";
    }

    // 3. KREIRANJE NARUDŽBE
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam Long productId,
                                 @RequestParam String customerName,
                                 @RequestParam String customerEmail) {
        Order savedOrder = orderService.createOrder(productId, customerName, customerEmail);
        return "redirect:/payment-success/" + savedOrder.getId();
    }

    // 4. USPJEŠNO PLAĆANJE
    // TODO (Selma): ovdje će doći PaymentTransaction; status se ne smije mijenjati običnim GET-om
    @GetMapping("/payment-success/{id}")
    public String paymentSuccess(@PathVariable Long id, Model model) {
        orderService.updateOrderStatus(id, "PLACENO");
        model.addAttribute("order", orderService.getOrderById(id));
        return "success";
    }

    // 5. ADMIN PREGLED SVIH NARUDŽBI
    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin-orders";
    }
}