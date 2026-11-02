package com.bank.creditbureauvirtualization.controller;

import com.bank.creditbureauvirtualization.model.LoanEligibilityResponse;
import com.bank.creditbureauvirtualization.service.LoanEligibilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan-eligibility")
public class LoanEligibilityController {

    private final LoanEligibilityService loanEligibilityService;

    public LoanEligibilityController(
            LoanEligibilityService loanEligibilityService
    ) {
        this.loanEligibilityService = loanEligibilityService;
    }


    @GetMapping("/{customerId}")
    public LoanEligibilityResponse checkEligibility(
            @PathVariable String customerId,
            @org.springframework.web.bind.annotation.RequestParam(
                    defaultValue = "normal"
            ) String scenario
    ) {
        return loanEligibilityService.checkEligibility(
                customerId, scenario
        );
    }

}