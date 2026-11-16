package com.example.bhpay.config;

import com.example.bhpay.model.Merchant;
import com.example.bhpay.model.Product;
import com.example.bhpay.repository.MerchantRepository;
import com.example.bhpay.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order; // NIJE naš entitet Order
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Puni bazu domaćim BH proizvodima. Izvršava se poslije MerchantDataInitializer (@Order(1)). */
@Component
@Order(2)
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;

    public ProductDataInitializer(ProductRepository productRepository,
                                  MerchantRepository merchantRepository) {
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Merchant kahva = merchantRepository.findByJib("4200000000011").orElseThrow();
        Merchant med = merchantRepository.findByJib("4200000000022").orElseThrow();
        Merchant caj = merchantRepository.findByJib("4200000000033").orElseThrow();
        Merchant una = merchantRepository.findByJib("4200000000044").orElseThrow();

        productRepository.saveAll(List.of(
                new Product("Domaća Bosanska Kafa (500g)", "Svježe mljevena bosanska kafa", "Kafa", new BigDecimal("12.50"), kahva),
                new Product("Bosanska Kafa mljevena (250g)", "Tradicionalna mješavina za džezvu", "Kafa", new BigDecimal("7.00"), kahva),

                new Product("Planinski Med (1kg)", "Med sa planine Igman", "Med", new BigDecimal("25.00"), med),
                new Product("Bagremov Med (1kg)", "Svijetli bagremov med", "Med", new BigDecimal("22.00"), med),
                new Product("Livadski Med (500g)", "Cvjetni med sa livada", "Med", new BigDecimal("14.00"), med),

                new Product("Domaći Čaj od Nane (100g)", "Sušena nana iz domaćeg uzgoja", "Čaj", new BigDecimal("5.00"), caj),
                new Product("Čaj od Kamilice (50g)", "Ručno brana kamilica", "Čaj", new BigDecimal("4.50"), caj),
                new Product("Čaj od Šipka (100g)", "Sušeni šipak", "Čaj", new BigDecimal("5.50"), caj),

                new Product("Domaći Ajvar (720g)", "Pečeni paprike, bez konzervansa", "Specijaliteti", new BigDecimal("8.50"), una),
                new Product("Kajmak (500g)", "Domaći mladi kajmak", "Specijaliteti", new BigDecimal("9.00"), una),
                new Product("Travnički Sir (400g)", "Polutvrdi bijeli sir", "Specijaliteti", new BigDecimal("11.00"), una),
                new Product("Rahat Lokum (250g)", "Tradicionalni rahat lokum", "Slatkiši", new BigDecimal("6.00"), una)
        ));
    }
}