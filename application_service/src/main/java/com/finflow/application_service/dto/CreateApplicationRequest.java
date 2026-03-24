package com.finflow.application_service.dto;

public class CreateApplicationRequest {
    private Double amount;
    public Double getAmount(){
        return amount;
    }
    public void setAmount(Double amount){
        this.amount=amount;
    }
}
