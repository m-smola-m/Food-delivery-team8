package com.team8.fooddelivery.domain;

import java.time.Instant;
public class PaymentMethod {
    private Long paymentMethodId;
    private Long clientId;
    private String methodType;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private Boolean isDefault;
    private Instant createdAt;
    private Boolean isActive;

    public PaymentMethod() {
        this.createdAt = Instant.now();
        this.isActive = true;
        this.isDefault = false;
    }

    public PaymentMethod(Long clientId, String methodType, String cardNumber, 
                        String cardHolderName, String expiryDate, String cvv) {
        this.clientId = clientId;
        this.methodType = methodType;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.createdAt = Instant.now();
        this.isActive = true;
        this.isDefault = false;
    }

    public Long getPaymentMethodId() { return paymentMethodId; }
    public Long getClientId() { return clientId; }
    public String getMethodType() { return methodType; }
    public String getCardNumber() { return cardNumber; }
    public String getCardHolderName() { return cardHolderName; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
    public Boolean getIsDefault() { return isDefault; }
    public Instant getCreatedAt() { return createdAt; }
    public Boolean getIsActive() { return isActive; }

    public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public void setMethodType(String methodType) { this.methodType = methodType; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
