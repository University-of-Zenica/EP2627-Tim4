package com.example.bhpay.controller;

import com.example.bhpay.model.Order;
import com.example.bhpay.model.PaymentTransaction;
import com.example.bhpay.model.Product;
import com.example.bhpay.model.ShoppingCart;
import com.example.bhpay.model.User;
import com.example.bhpay.repository.OrderRepository;
import com.example.bhpay.repository.PaymentTransactionRepository;
import com.example.bhpay.service.OrderService;
import com.example.bhpay.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class CheckoutController {

    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentTransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    public CheckoutController(OrderService orderService,
                              ProductService productService,
                              PaymentTransactionRepository transactionRepository,
                              OrderRepository orderRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }

    private String redirectToLoginWithMessage(String msg) {
        String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        return "redirect:/login?message=" + encoded;
    }

    private ShoppingCart getOrCreateCart(HttpSession session, User user) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null || !user.getId().equals(cart.getOwnerUserId())) {
            cart = new ShoppingCart(user.getId());
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("loggedUser", loggedUser);
        if (loggedUser != null) {
            model.addAttribute("cartCount", getOrCreateCart(session, loggedUser).getTotalCount());
        } else {
            model.addAttribute("cartCount", 0);
        }
        return "index";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, HttpSession session) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return redirectToLoginWithMessage("Prijavite se da biste nastavili kupovinu");
        }

        Product product = productService.getProductById(productId);
        if (product != null) {
            ShoppingCart cart = getOrCreateCart(session, loggedUser);
            cart.addProduct(product);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return redirectToLoginWithMessage("Prijavite se da biste nastavili kupovinu");
        }

        ShoppingCart cart = getOrCreateCart(session, loggedUser);
        model.addAttribute("cart", cart);
        model.addAttribute("loggedUser", loggedUser);
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam(required = false) Long productId,
                               HttpSession session,
                               Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return redirectToLoginWithMessage("Prijavite se da biste nastavili kupovinu");
        }

        ShoppingCart cart = getOrCreateCart(session, loggedUser);
        if (productId != null) {
            Product product = productService.getProductById(productId);
            if (product == null) {
                return "redirect:/";
            }
            model.addAttribute("product", product);
        } else if (!cart.getItems().isEmpty()) {
            model.addAttribute("product", cart.getItems().get(0).getProduct());
        } else {
            return "redirect:/";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("loggedUser", loggedUser);
        return "checkout";
    }

    @PostMapping("/process-payment")
    public String processPayment(@RequestParam Long productId,
                                 @RequestParam String customerName,
                                 @RequestParam String customerEmail,
                                 HttpSession session) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return redirectToLoginWithMessage("Prijavite se da biste nastavili kupovinu");
        }

        Order savedOrder = orderService.createOrder(productId, customerName, customerEmail);
        savedOrder.setCustomer(loggedUser);
        orderRepository.save(savedOrder);

        ShoppingCart cart = getOrCreateCart(session, loggedUser);
        cart.clear();

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
    public String adminOrders(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) {
            return redirectToLoginWithMessage("Prijavite se kao trgovac da biste pristupili narudžbama");
        }
        if (!"TRGOVAC".equals(loggedUser.getRole())) {
            return "redirect:/";
        }
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("loggedUser", loggedUser);
        return "admin-orders";
    }
}