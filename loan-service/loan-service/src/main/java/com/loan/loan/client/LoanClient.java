package com.loan.loan.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "loan-service")
public interface LoanClient {
    @PutMapping("/api/loans/update-remaining")
    void updateLoanRemainingAmount(@RequestParam Long loanId, 
                                 @RequestParam BigDecimal amount);
}