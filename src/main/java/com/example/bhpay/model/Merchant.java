package com.example.bhpay.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domaći trgovac (merchant) koji prima uplate preko BHPay sistema.
 * Proizvodi (Product) i narudžbe (Order) se vezuju na trgovca preko @ManyToOne.
 */
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;                 // naziv trgovca, npr. "Bosanska Kahva d.o.o."

    @Column(nullable = false, unique = true, length = 13)
    private String jib;                  // JIB - identifikacioni broj, 13 cifara

    @Column(nullable = false, unique = true, length = 20)
    private String iban;                 // BH IBAN: "BA" + 18 cifara = 20 znakova

    // Ugovorena provizija u procentima, npr. 2.50 znači 2,5% od iznosa transakcije
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    private String city;

    private boolean active = true;

    public Merchant() {}

    public Merchant(String name, String jib, String iban, BigDecimal commissionRate, String city) {
        this.name = name;
        this.jib = jib;
        this.iban = iban;
        this.commissionRate = commissionRate;
        this.city = city;
        this.active = true;
    }

    /** Provizija BHPay-a za dati iznos (KM), zaokruženo na 2 decimale. */
    public BigDecimal calculateCommission(BigDecimal amount) {
        return amount.multiply(commissionRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /** Iznos koji se isplaćuje trgovcu nakon odbitka provizije. */
    public BigDecimal calculateNetAmount(BigDecimal amount) {
        return amount.subtract(calculateCommission(amount));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJib() { return jib; }
    public void setJib(String jib) { this.jib = jib; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}