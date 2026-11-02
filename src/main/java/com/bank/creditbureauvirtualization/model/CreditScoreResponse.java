package com.bank.creditbureauvirtualization.model;

public record CreditScoreResponse(String customerId, int creditScore, String status) {
}
