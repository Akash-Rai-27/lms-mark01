package com.loan.loan.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private BigDecimal remainingAmount;
    
    @Column(nullable = false)
    private BigDecimal interestRate;
    
    @Column(nullable = false)
    private Integer termInMonths;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    
    @Column(nullable = false)
    private Date startDate;
    
    @Column(nullable = false)
    private Date dueDate;

    public Loan() {
        this.status = LoanStatus.PENDING; // Default status
//        this.startDate = new Date(); // Default start date
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermInMonths() {
        return termInMonths;
    }

    public void setTermInMonths(Integer termInMonths) {
        this.termInMonths = termInMonths;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.startDate == null) {
            this.startDate = new Date();
        }
        if (this.dueDate == null && this.termInMonths != null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, this.termInMonths);
            this.dueDate = cal.getTime();
        }
        if (this.remainingAmount == null && this.amount != null) {
            this.remainingAmount = this.amount;
        }
        if (this.status == null) {
            this.status = LoanStatus.PENDING;
        }
    }
    
    
}