package com.example.bhpay.controller;

import com.example.bhpay.model.User;
import com.example.bhpay.repository.UserRepository;
import com.example.bhpay.service.PasswordHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String message, Model model) {
        if (message != null) {
            model.addAttribute("message", message);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        String hashedPassword = PasswordHelper.hashSha256(password);

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(hashedPassword)) {
            model.addAttribute("error", "Pogrešan email ili lozinka.");
            return "login";
        }

        session.setAttribute("loggedUser", userOpt.get());
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           HttpSession session,
                           Model model) {
        String cleanEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(cleanEmail)) {
            model.addAttribute("error", "Korisnik sa ovim emailom već postoji.");
            return "register";
        }

        User user = new User(
                fullName.trim(),
                cleanEmail,
                PasswordHelper.hashSha256(password),
                "KUPAC"
        );
        userRepository.save(user);
        session.setAttribute("loggedUser", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
