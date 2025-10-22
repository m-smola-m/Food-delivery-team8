package com.team8.fooddelivery.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;
    
    @Column(nullable = false)
    private Long clientId;
    
    @Column(nullable = false)
    private String methodType;
    
    @Column(nullable = false)
    private String cardNumber;
    
    @Column(nullable = false)
    private String cardHolderName;
    
    @Column(nullable = false)
    private String expiryDate;
    
    @Column(nullable = false)
    private String cvv;
    
    @Column(nullable = false)
    private Boolean isDefault;
    
    @Column(nullable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
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
