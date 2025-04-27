package com.loan.payment.client;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class LoanClientFallback implements LoanClient {
    @Override
    public void updateLoanRemainingAmount(Long loanId, BigDecimal amount) {
        // Log the failure or implement alternative logic
        System.err.println("Failed to update loan remaining amount - circuit breaker active");
    }
}