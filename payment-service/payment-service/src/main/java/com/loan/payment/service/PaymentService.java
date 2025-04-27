package com.loan.payment.service;

import com.loan.payment.client.LoanClient;
import com.loan.payment.exception.PaymentNotFoundException;
import com.loan.payment.exception.UnauthorizedAccessException;
import com.loan.payment.model.Payment;
import com.loan.payment.model.PaymentStatus;
import com.loan.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanClient loanClient;

    public PaymentService(PaymentRepository paymentRepository, LoanClient loanClient) {
        this.paymentRepository = paymentRepository;
        this.loanClient = loanClient;
    }

    public Payment createPayment(Payment payment) {
        payment.setPaymentDate(new Date());
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByLoan(Long loanId, Long customerId) {
        List<Payment> payments = paymentRepository.findByLoanId(loanId);
        if (!payments.isEmpty() && !payments.get(0).getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("You are not authorized to access these payments");
        }
        return payments;
    }

    public List<Payment> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    public Payment processPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
        
        // Here you would integrate with a payment gateway in a real application
        payment.setStatus(PaymentStatus.COMPLETED);
        
        // Update loan remaining amount
        loanClient.updateLoanRemainingAmount(payment.getLoanId(), payment.getAmount());
        
        return paymentRepository.save(payment);
    }
}