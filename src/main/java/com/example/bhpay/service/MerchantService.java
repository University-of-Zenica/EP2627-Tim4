package com.example.bhpay.service;

import com.example.bhpay.model.Merchant;
import com.example.bhpay.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public Merchant createMerchant(String name, String jib, String iban,
                                   BigDecimal commissionRate, String city) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Naziv trgovca je obavezan.");
        }
        if (jib == null || !jib.matches("\\d{13}")) {
            throw new IllegalArgumentException("JIB mora imati tačno 13 cifara.");
        }
        if (!isValidBhIban(iban)) {
            throw new IllegalArgumentException("IBAN nije ispravan (očekuje se BA + 18 cifara, validan kontrolni broj).");
        }
        if (commissionRate == null || commissionRate.signum() < 0
                || commissionRate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Provizija mora biti između 0 i 100 %.");
        }
        if (merchantRepository.existsByJib(jib)) {
            throw new IllegalArgumentException("Trgovac sa ovim JIB-om već postoji.");
        }
        if (merchantRepository.existsByIban(iban)) {
            throw new IllegalArgumentException("Trgovac sa ovim IBAN-om već postoji.");
        }
        return merchantRepository.save(new Merchant(name, jib, iban, commissionRate, city));
    }

    public Merchant getMerchantById(Long id) {
        return merchantRepository.findById(id).orElse(null);
    }

    public Merchant getMerchantByJib(String jib) {
        return merchantRepository.findByJib(jib).orElse(null);
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    /** IBAN za BiH: "BA" + 18 cifara; kontrolni broj po ISO 13616 (mod 97 == 1). */
    public static boolean isValidBhIban(String iban) {
        if (iban == null || !iban.matches("BA\\d{18}")) {
            return false;
        }
        // Premjesti prva 4 znaka na kraj, slova -> brojevi (B=11, A=10)
        String rearranged = iban.substring(4) + "1110" + iban.substring(2, 4);
        return new BigInteger(rearranged).mod(BigInteger.valueOf(97)).intValue() == 1;
    }
}