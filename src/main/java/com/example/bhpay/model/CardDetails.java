package com.example.bhpay.model;

import jakarta.persistence.*;

@Entity
@Table(name = "card_details")
public class CardDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cardHolderName;

    @Column(nullable = false)
    private String maskedCardNumber;

    @Column(nullable = false)
    private String cardBrand;

    @Column(nullable = false)
    private String expiryDate;

    @Column(nullable = false, unique = true)
    private String paymentToken;

    public CardDetails() {
    }

    public CardDetails(String cardHolderName, String maskedCardNumber, String cardBrand, String expiryDate, String paymentToken) {
        this.cardHolderName = cardHolderName;
        this.maskedCardNumber = maskedCardNumber;
        this.cardBrand = cardBrand;
        this.expiryDate = expiryDate;
        this.paymentToken = paymentToken;
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getMaskedCardNumber() { return maskedCardNumber; }
    public void setMaskedCardNumber(String maskedCardNumber) { this.maskedCardNumber = maskedCardNumber; }

    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getPaymentToken() { return paymentToken; }
    public void setPaymentToken(String paymentToken) { this.paymentToken = paymentToken; }
}