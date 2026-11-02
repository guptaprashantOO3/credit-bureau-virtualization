package com.bank.creditbureauvirtualization.model;

public record LoanEligibilityResponse(String customerId, int creditScore, boolean eligible, String message) {
}
