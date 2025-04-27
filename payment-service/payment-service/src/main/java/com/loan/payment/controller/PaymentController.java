package com.loan.payment.controller;

import com.loan.payment.model.Payment;
import com.loan.payment.model.PaymentStatus;
import com.loan.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment, 
                                                @AuthenticationPrincipal Long userId) {
        payment.setCustomerId(userId);
        payment.setStatus(PaymentStatus.PENDING);
        Payment createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.ok(createdPayment);
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Payment>> getPaymentsByLoan(@PathVariable Long loanId, 
                                                          @AuthenticationPrincipal Long userId) {
        List<Payment> payments = paymentService.getPaymentsByLoan(loanId, userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getCustomerPayments(@AuthenticationPrincipal Long userId) {
        List<Payment> payments = paymentService.getPaymentsByCustomer(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<Payment> processPayment(@PathVariable Long id) {
        Payment payment = paymentService.processPayment(id);
        return ResponseEntity.ok(payment);
    }
}