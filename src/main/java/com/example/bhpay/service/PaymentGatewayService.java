package com.example.bhpay.service;

import com.example.bhpay.model.*;
import com.example.bhpay.repository.CardDetailsRepository;
import com.example.bhpay.repository.OrderRepository;
import com.example.bhpay.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
public class PaymentGatewayService {

    private static final BigDecimal EUR_EXCHANGE_RATE = new BigDecimal("1.95583");
    private static final BigDecimal THREE_DS_THRESHOLD = new BigDecimal("15.00");

    private final PaymentTransactionRepository transactionRepository;
    private final CardDetailsRepository cardDetailsRepository;
    private final OrderRepository orderRepository;

    public PaymentGatewayService(PaymentTransactionRepository transactionRepository,
                                 CardDetailsRepository cardDetailsRepository,
                                 OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.cardDetailsRepository = cardDetailsRepository;
        this.orderRepository = orderRepository;
    }

    /** Kreira početnu PENDING transakciju za narudžbu ako već ne postoji. */
    @Transactional
    public PaymentTransaction initiateTransaction(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Narudžba ne postoji: " + orderId));

        return transactionRepository.findByOrderId(orderId).orElseGet(() -> {
            PaymentTransaction tx = new PaymentTransaction();
            tx.setOrder(order);
            tx.setAmountBam(order.getAmount());
            tx.setAmountEur(order.getAmount().divide(EUR_EXCHANGE_RATE, 2, RoundingMode.HALF_UP));
            tx.setTransactionReference("BHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            tx.setStatus(TransactionStatus.PENDING);
            return transactionRepository.save(tx);
        });
    }

    @Transactional
    public PaymentTransaction submitCardDetails(String txRef, String cardHolderName,
                                                String rawCardNumber, String expiryDate, String cvv) {
        PaymentTransaction tx = getByReference(txRef);

        if (tx.getStatus() == TransactionStatus.APPROVED) {
            throw new IllegalStateException("Ova transakcija je već uspješno plaćena.");
        }

        String cleanNumber = rawCardNumber == null ? "" : rawCardNumber.replaceAll("[\\s-]+", "");

        if (cardHolderName == null || cardHolderName.trim().length() < 3) {
            throw new IllegalArgumentException("Unesite ispravno ime i prezime vlasnika kartice.");
        }
        if (!isValidLuhn(cleanNumber)) {
            throw new IllegalArgumentException("Broj kartice nije validan (neuspjela Luhn provjera).");
        }
        if (!isValidExpiryDate(expiryDate)) {
            throw new IllegalArgumentException("Kartica je istekla ili format datuma (MM/YY) nije ispravan.");
        }
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV sigurnosni kod mora imati 3 ili 4 cifre.");
        }

        String brand = detectCardBrand(cleanNumber);
        String masked = "**** **** **** " + cleanNumber.substring(cleanNumber.length() - 4);
        String token = "tok_" + UUID.randomUUID().toString().replace("-", "");

        CardDetails card = new CardDetails(cardHolderName.trim().toUpperCase(), masked, brand, expiryDate.trim(), token);
        cardDetailsRepository.save(card);
        tx.setCardDetails(card);

        if (tx.getAmountBam().compareTo(THREE_DS_THRESHOLD) <= 0) {
            tx.setThreeDSecureVerified(false);
            return authorizeWithBank(tx, cleanNumber);
        }

        tx.setBankResponseMessage("Potrebna 3D Secure verifikacija.");
        return transactionRepository.save(tx);
    }

    @Transactional
    public PaymentTransaction verifyThreeDSecure(String txRef, String otpCode) {
        PaymentTransaction tx = getByReference(txRef);

        if (tx.getCardDetails() == null) {
            throw new IllegalStateException("Podaci kartice nisu uneseni.");
        }
        if (!"123456".equals(otpCode)) {
            throw new IllegalArgumentException("Neispravan 3D Secure OTP kod. (Testni kod je 123456)");
        }

        tx.setThreeDSecureVerified(true);
        String last4 = tx.getCardDetails().getMaskedCardNumber().substring(15);
        return authorizeWithBank(tx, last4);
    }

    private PaymentTransaction authorizeWithBank(PaymentTransaction tx, String cardNumberOrLast4) {
        Order order = tx.getOrder();

        if (cardNumberOrLast4.endsWith("0002")) {
            tx.setStatus(TransactionStatus.DECLINED);
            tx.setBankResponseMessage("Odbijeno (Kod 51): Nedovoljno sredstava na računu.");
            order.setStatus("OTKAZANO");
        } else if (cardNumberOrLast4.endsWith("9997")) {
            tx.setStatus(TransactionStatus.DECLINED);
            tx.setBankResponseMessage("Odbijeno (Kod 05): Kartica je ograničena ili blokirana.");
            order.setStatus("OTKAZANO");
        } else {
            tx.setStatus(TransactionStatus.APPROVED);
            tx.setBankResponseMessage("Odobreno (Kod 00): Transakcija uspješno realizovana.");
            order.setStatus("PLACENO");
        }

        tx.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
        return transactionRepository.save(tx);
    }

    public boolean requiresThreeDSecure(PaymentTransaction tx) {
        return tx.getAmountBam().compareTo(THREE_DS_THRESHOLD) > 0 && !tx.isThreeDSecureVerified();
    }

    public PaymentTransaction getByReference(String txRef) {
        return transactionRepository.findByTransactionReference(txRef)
                .orElseThrow(() -> new IllegalArgumentException("Transakcija nije pronađena: " + txRef));
    }

    public static boolean isValidLuhn(String number) {
        if (number == null || !number.matches("\\d{13,19}")) {
            return false;
        }
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static String detectCardBrand(String number) {
        if (number.startsWith("4")) return "VISA";
        if (number.matches("^5[1-5].*") || number.matches("^2[2-7].*")) return "MASTERCARD";
        return "OSTALO";
    }

    public static boolean isValidExpiryDate(String expiry) {
        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth exp = YearMonth.parse(expiry, formatter);
            return !exp.isBefore(YearMonth.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}