package com.loan.loan.model;

public enum LoanStatus {
    PENDING,       // Loan application is under review
    APPROVED,      // Loan has been approved
    REJECTED,      // Loan application was rejected
    ACTIVE,        // Loan is active and being repaid
    PAID,          // Loan has been fully paid
    DEFAULTED,     // Loan is in default (payments missed)
    CANCELLED;     // Loan was cancelled before disbursement
}