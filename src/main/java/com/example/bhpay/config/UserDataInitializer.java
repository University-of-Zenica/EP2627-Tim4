package com.example.bhpay.config;

import com.example.bhpay.model.User;
import com.example.bhpay.repository.UserRepository;
import com.example.bhpay.service.PasswordHelper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByEmail("kupac@bhpay.ba")) {
                userRepository.save(new User(
                        "Kupac Test",
                        "kupac@bhpay.ba",
                        PasswordHelper.hashSha256("123456"),
                        "KUPAC"
                ));
            }
            if (!userRepository.existsByEmail("trgovac@bhpay.ba")) {
                userRepository.save(new User(
                        "Trgovac Test",
                        "trgovac@bhpay.ba",
                        PasswordHelper.hashSha256("123456"),
                        "TRGOVAC"
                ));
            }
        };
    }
}
