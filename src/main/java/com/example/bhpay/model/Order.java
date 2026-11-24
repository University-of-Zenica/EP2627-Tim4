package com.example.bhpay.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    // Trgovac kojem ide uplata (preuzet sa proizvoda u trenutku narudžbe)
    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    private String productName;   // naziv u trenutku kupovine

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;    // iznos u KM, uzet iz baze, ne od kupca

    private String customerName;
    private String customerEmail;
    private String status;        // "NA_CEKANJU", "PLACENO", "OTKAZANO"

    public Order() {}

    public Order(Product product, String customerName, String customerEmail) {
        this.product = product;
        this.merchant = product.getMerchant();
        this.productName = product.getName();
        this.amount = product.getPrice();
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = "NA_CEKANJU";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Merchant getMerchant() { return merchant; }
    public void setMerchant(Merchant merchant) { this.merchant = merchant; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}