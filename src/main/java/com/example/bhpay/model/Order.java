package com.example.bhpay.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private double amount; // Iznos u KM
    private String customerName;
    private String customerEmail;
    private String status; // npr. "NA_CEKANJU", "PLACENO", "OTKAZANO"

    // Prazan konstruktor obavezan za JPA
    public Order() {}

    public Order(String productName, double amount, String customerName, String customerEmail, String status) {
        this.productName = productName;
        this.amount = amount;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = status;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

