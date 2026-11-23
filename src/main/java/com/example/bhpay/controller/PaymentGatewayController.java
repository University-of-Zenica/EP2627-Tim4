package com.example.bhpay.controller;

import com.example.bhpay.model.PaymentTransaction;
import com.example.bhpay.model.TransactionStatus;
import com.example.bhpay.service.PaymentGatewayService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gateway")
public class PaymentGatewayController {

    private final PaymentGatewayService gatewayService;

    public PaymentGatewayController(PaymentGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/pay/{orderId}")
    public String showPaymentForm(@PathVariable Long orderId, Model model) {
        PaymentTransaction tx = gatewayService.initiateTransaction(orderId);
        if (tx.getStatus() == TransactionStatus.APPROVED) {
            return "redirect:/payment-success/" + orderId;
        }
        model.addAttribute("tx", tx);
        model.addAttribute("order", tx.getOrder());
        return "payment-gateway";
    }

    @PostMapping("/submit-card")
    public String submitCard(@RequestParam String txRef,
                             @RequestParam String cardHolderName,
                             @RequestParam String cardNumber,
                             @RequestParam String expiryDate,
                             @RequestParam String cvv,
                             RedirectAttributes redirectAttributes) {
        try {
            PaymentTransaction tx = gatewayService.submitCardDetails(
                    txRef, cardHolderName, cardNumber, expiryDate, cvv);

            if (tx.getStatus() == TransactionStatus.PENDING && gatewayService.requiresThreeDSecure(tx)) {
                return "redirect:/gateway/3ds/" + tx.getTransactionReference();
            }

            if (tx.getStatus() == TransactionStatus.DECLINED) {
                return "redirect:/gateway/declined/" + tx.getTransactionReference();
            }

            return "redirect:/payment-success/" + tx.getOrder().getId();

        } catch (IllegalArgumentException | IllegalStateException e) {
            PaymentTransaction tx = gatewayService.getByReference(txRef);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gateway/pay/" + tx.getOrder().getId();
        }
    }

    @GetMapping("/3ds/{txRef}")
    public String showThreeDSecure(@PathVariable String txRef, Model model) {
        PaymentTransaction tx = gatewayService.getByReference(txRef);
        if (tx.getCardDetails() == null) {
            return "redirect:/gateway/pay/" + tx.getOrder().getId();
        }
        if (tx.getStatus() == TransactionStatus.APPROVED) {
            return "redirect:/payment-success/" + tx.getOrder().getId();
        }
        if (tx.getStatus() == TransactionStatus.DECLINED) {
            return "redirect:/gateway/declined/" + txRef;
        }
        model.addAttribute("tx", tx);
        model.addAttribute("order", tx.getOrder());
        return "payment-3ds";
    }

    @PostMapping("/3ds/verify")
    public String verifyThreeDSecure(@RequestParam String txRef,
                                     @RequestParam String otpCode,
                                     RedirectAttributes redirectAttributes) {
        try {
            PaymentTransaction tx = gatewayService.verifyThreeDSecure(txRef, otpCode);
            if (tx.getStatus() == TransactionStatus.DECLINED) {
                return "redirect:/gateway/declined/" + tx.getTransactionReference();
            }
            return "redirect:/payment-success/" + tx.getOrder().getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/gateway/3ds/" + txRef;
        }
    }

    @GetMapping("/declined/{txRef}")
    public String showDeclined(@PathVariable String txRef, Model model) {
        PaymentTransaction tx = gatewayService.getByReference(txRef);
        model.addAttribute("tx", tx);
        model.addAttribute("order", tx.getOrder());
        return "payment-declined";
    }
}