package com.finflow.application_service.dto;

public class CreateApplicationRequest {
    private Double amount;
    private String loanType;
    private Integer tenure;
    private Double income;
    private String employmentType;

    public Double getAmount(){
        return amount;
    }
    public void setAmount(Double amount){
        this.amount=amount;
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
