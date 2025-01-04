package org.simbrella.loanservice.utils;

import org.simbrella.loanservice.utils.dtos.LoanDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanCalculator {


    public static LoanDetails calculateLoanDetails(BigDecimal loanAmount, Integer loanTermMonths, BigDecimal annualIncome) {
        LoanDetails loanDetails = new LoanDetails();
        loanDetails.setLoanAmount(loanAmount);
        loanDetails.setLoanTermMonths(loanTermMonths);
        loanDetails.setAnnualIncome(annualIncome);

        loanDetails.setInterestRate(determineInterestRate(loanAmount, annualIncome));
        loanDetails.setMonthlyInstallment(calculateEMI(loanAmount, loanDetails.getInterestRate(), loanTermMonths));
        loanDetails.setTotalInterest(calculateTotalInterest(loanDetails.getMonthlyInstallment(), loanAmount, loanTermMonths));

        return loanDetails;
    }

    private static BigDecimal determineInterestRate(BigDecimal loanAmount, BigDecimal annualIncome) {
        if (loanAmount.compareTo(BigDecimal.valueOf(5000)) <= 0 && annualIncome.compareTo(BigDecimal.valueOf(20000)) >= 0) {
            return BigDecimal.valueOf(0.05);
        } else if (loanAmount.compareTo(BigDecimal.valueOf(20000)) <= 0 && annualIncome.compareTo(BigDecimal.valueOf(30000)) >= 0) {
            return BigDecimal.valueOf(0.10);
        } else {
            return BigDecimal.valueOf(0.15);
        }
    }

    private static BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, int months) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply((BigDecimal.ONE.add(monthlyRate)).pow(months));
        BigDecimal denominator = (BigDecimal.ONE.add(monthlyRate)).pow(months).subtract(BigDecimal.ONE);
        return numerator.divide(denominator, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalInterest(BigDecimal monthlyInstallment, BigDecimal principal, int months) {
        return monthlyInstallment.multiply(BigDecimal.valueOf(months)).subtract(principal);
    }

    public static void main(String[] args) {
        LoanCalculator calculator = new LoanCalculator();

        // Example loan application
        BigDecimal loanAmount = BigDecimal.valueOf(10000);
        Integer loanTermMonths = 24;
        BigDecimal annualIncome = BigDecimal.valueOf(35000);

        LoanDetails details = calculator.calculateLoanDetails(loanAmount, loanTermMonths, annualIncome);

        // Output loan details
        System.out.println(details);
    }
}

