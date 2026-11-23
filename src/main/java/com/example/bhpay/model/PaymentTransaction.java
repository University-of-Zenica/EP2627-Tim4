package com.example.bhpay.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionReference;

    @Column(nullable = false)
    private BigDecimal amountBam;

    private BigDecimal amountEur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String bankResponseMessage;

    private boolean threeDSecureVerified;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private CardDetails cardDetails;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    public PaymentTransaction() {
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public BigDecimal getAmountBam() { return amountBam; }
    public void setAmountBam(BigDecimal amountBam) { this.amountBam = amountBam; }

    public BigDecimal getAmountEur() { return amountEur; }
    public void setAmountEur(BigDecimal amountEur) { this.amountEur = amountEur; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getBankResponseMessage() { return bankResponseMessage; }
    public void setBankResponseMessage(String bankResponseMessage) { this.bankResponseMessage = bankResponseMessage; }

    public boolean isThreeDSecureVerified() { return threeDSecureVerified; }
    public void setThreeDSecureVerified(boolean threeDSecureVerified) { this.threeDSecureVerified = threeDSecureVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public CardDetails getCardDetails() { return cardDetails; }
    public void setCardDetails(CardDetails cardDetails) { this.cardDetails = cardDetails; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}