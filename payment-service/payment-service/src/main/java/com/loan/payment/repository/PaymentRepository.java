package com.loan.payment.repository;

import com.loan.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoanId(Long loanId);
    List<Payment> findByCustomerId(Long customerId);
}