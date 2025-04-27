package com.loan.loan.controller;

import com.loan.loan.model.Loan;
import com.loan.loan.model.LoanStatus;
import com.loan.loan.security.UserPrincipal;
import com.loan.loan.service.LoanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(
        @RequestBody Loan loan,
        @AuthenticationPrincipal String username) {
        
        // 1. Set customer ID mapping
        Map<String, Long> userMap = new HashMap<>();
        userMap.put("customer1", 1L);
        userMap.put("customer2", 2L);
        
        Long customerId = userMap.get(username);
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to create loans");
        }
        loan.setCustomerId(customerId);

        // 2. Set required fields
        loan.setStatus(LoanStatus.PENDING);
        loan.setStartDate(new Date());
        
        // Calculate due date
        if (loan.getTermInMonths() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, loan.getTermInMonths());
            loan.setDueDate(calendar.getTime());
        }
        
        // Initialize remaining amount
        if (loan.getRemainingAmount() == null && loan.getAmount() != null) {
            loan.setRemainingAmount(loan.getAmount());
        }

        // Validate required fields
        if (loan.getAmount() == null || loan.getInterestRate() == null || 
            loan.getTermInMonths() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Amount, interest rate and term are required");
        }

        Loan createdLoan = loanService.createLoan(loan);
        return ResponseEntity.ok(createdLoan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoan(@PathVariable Long id, 
                                       @AuthenticationPrincipal Long userId) {
        Loan loan = loanService.getLoan(id, userId);
        return ResponseEntity.ok(loan);
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getCustomerLoans(@AuthenticationPrincipal Long userId) {
        List<Loan> loans = loanService.getLoansByCustomerId(userId);
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Loan> approveLoan(@PathVariable Long id) {
        Loan loan = loanService.approveLoan(id);
        return ResponseEntity.ok(loan);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Loan> rejectLoan(@PathVariable Long id) {
        Loan loan = loanService.rejectLoan(id);
        return ResponseEntity.ok(loan);
    }
}