package com.example.bhpay.config;

import com.example.bhpay.model.Merchant;
import com.example.bhpay.repository.MerchantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order; // pažnja: ovo NIJE naš entitet Order
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Puni bazu testnim trgovcima (izmišljeni podaci). Mora se izvršiti PRIJE punjenja proizvoda
 * (Aidin initializer treba imati @Order(2)), jer proizvodi referenciraju trgovca.
 */
@Component
@Order(1)
public class MerchantDataInitializer implements CommandLineRunner {

    private final MerchantRepository merchantRepository;

    public MerchantDataInitializer(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public void run(String... args) {
        if (merchantRepository.count() > 0) {
            return;
        }
        merchantRepository.saveAll(List.of(
                new Merchant("Bosanska Kahva d.o.o.", "4200000000011",
                        "BA411990490000000011", new BigDecimal("2.50"), "Sarajevo"),
                new Merchant("Planinski Med Igman", "4200000000022",
                        "BA093380004800000022", new BigDecimal("2.20"), "Konjic"),
                new Merchant("Zeleni Čaj Bosne d.o.o.", "4200000000033",
                        "BA271610000000000033", new BigDecimal("3.00"), "Tuzla"),
                new Merchant("Domaći Specijaliteti Una", "4200000000044",
                        "BA675520000000000044", new BigDecimal("1.90"), "Bihać")
        ));
    }
}