package com.loan.payment.model;

public enum PaymentStatus {
    PENDING,    // Payment initiated but not processed
    COMPLETED,  // Payment successfully processed
    FAILED,     // Payment processing failed
    REFUNDED    // Payment was refunded
}