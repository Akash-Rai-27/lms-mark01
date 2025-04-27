package com.loan.loan.service;

import com.loan.loan.exception.LoanNotFoundException;
import com.loan.loan.exception.UnauthorizedAccessException;
import com.loan.loan.model.Loan;
import com.loan.loan.model.LoanStatus;
import com.loan.loan.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public Loan createLoan(Loan loan) {
        loan.setStartDate(new Date());
        // Calculate due date based on term
        return loanRepository.save(loan);
    }

    public Loan getLoan(Long id, Long customerId) {
        Optional<Loan> loanOptional = loanRepository.findById(id);
        if (loanOptional.isEmpty()) {
            throw new LoanNotFoundException("Loan not found with id: " + id);
        }
        
        Loan loan = loanOptional.get();
        if (!loan.getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this loan");
        }
        
        return loan;
    }

    public List<Loan> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public Loan approveLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with id: " + id));
        
        loan.setStatus(LoanStatus.APPROVED);
        return loanRepository.save(loan);
    }

    public Loan rejectLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with id: " + id));
        
        loan.setStatus(LoanStatus.REJECTED);
        return loanRepository.save(loan);
    }
}