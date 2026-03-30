package com.finflow.application_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="loan_applications")
public class LoanApplication {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double amount;

    private String loanType;

    private Integer tenure;

    private Double income;

    private String employmentType;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;


    public LoanApplication(Long id, Long userId, Double amount, String loanType, Integer tenure, Double income, String employmentType, ApplicationStatus status, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.loanType = loanType;
        this.tenure = tenure;
        this.income = income;
        this.employmentType = employmentType;
        this.status = status;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    public LoanApplication() {

    }

    public LoanApplication(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
        this.status = ApplicationStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateTimestamp(){
        this.updateAt=LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }
}
