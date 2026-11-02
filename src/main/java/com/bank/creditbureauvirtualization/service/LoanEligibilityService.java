package com.bank.creditbureauvirtualization.service;

import com.bank.creditbureauvirtualization.client.CreditBureauClient;
import com.bank.creditbureauvirtualization.model.CreditScoreResponse;
import com.bank.creditbureauvirtualization.model.LoanEligibilityResponse;
import org.springframework.stereotype.Service;

@Service
public class LoanEligibilityService {

    private static final int MINIMUM_CREDIT_SCORE = 750;

    private final CreditBureauClient creditBureauClient;

    public LoanEligibilityService(
            CreditBureauClient creditBureauClient
    ) {
        this.creditBureauClient = creditBureauClient;
    }


    public LoanEligibilityResponse checkEligibility(
            String customerId,
            String scenario
    ) {
        CreditScoreResponse creditScore =
                creditBureauClient.getCreditScore(
                        customerId, scenario
                );

        boolean eligible =
                creditScore.creditScore() >= MINIMUM_CREDIT_SCORE;

        String message = eligible
                ? "Customer meets the demo credit score requirement"
                : "Customer does not meet the demo credit score requirement";

        return new LoanEligibilityResponse(
                customerId,
                creditScore.creditScore(),
                eligible,
                message
        );
    }
}