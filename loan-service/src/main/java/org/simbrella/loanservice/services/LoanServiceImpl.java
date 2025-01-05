package org.simbrella.loanservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.simbrella.loanservice.dtos.User;
import org.simbrella.loanservice.dtos.requests.LoanApplicationRequest;
import org.simbrella.loanservice.dtos.requests.UpdateLoanStatusRequest;
import org.simbrella.loanservice.dtos.responses.LoanApplicationResponse;
import org.simbrella.loanservice.exceptions.InvalidDetailsException;
import org.simbrella.loanservice.exceptions.LoanTechException;
import org.simbrella.loanservice.exceptions.NotFoundException;
import org.simbrella.loanservice.models.Loan;
import org.simbrella.loanservice.models.LoanStatus;
import org.simbrella.loanservice.repositories.LoanRepository;
import org.simbrella.loanservice.utils.dtos.LoanDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import static org.simbrella.loanservice.models.LoanStatus.*;
import static org.simbrella.loanservice.utils.LoanCalculator.calculateLoanDetails;
import static org.simbrella.loanservice.utils.ValidationUtils.isValidEmail;
import static org.simbrella.loanservice.utils.ValidationUtils.isValidPhoneNumber;

@Slf4j
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;

    @Value("${user-service-url}")
    String serviceName;

    private final DiscoveryClient discoveryClient;

    public LoanServiceImpl(LoanRepository loanRepository, ModelMapper modelMapper, WebClient webClient, DiscoveryClient discoveryClient) {
        this.loanRepository = loanRepository;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public LoanApplicationResponse applyForLoan(LoanApplicationRequest request) {
        validateDetails(request.getEmail(), request.getDateOfBirth(), request.getPhoneNumber());
        LoanDetails loanDetails = calculateLoanDetails(request.getLoanAmount(), request.getLoanTermMonths(), request.getAnnualIncome());
        User user = fetchUserDetails(request.getEmail());
        Loan loan = saveLoanDetails(loanDetails, user.getId());
        log.info("Loan applied for by {}", user.getId());
        log.info("Loan with id: {} is pending", loan.getId());
        return mapToLoanApplicationResponse(loan, user);
    }

    @Override
    public User getUser(String email) {
        try {
            String serviceUrl = getServiceUrl(serviceName);
            return webClient.get().uri(serviceUrl + "/api/v1/users/{email}", email).retrieve().bodyToMono(User.class).block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("User not found with email: " + email);
            } else if (e.getStatusCode().is4xxClientError()) {
                log.error("Client error {}", e.getResponseBodyAsString());
                throw new LoanTechException("Client error: " + e.getResponseBodyAsString());
            } else if (e.getStatusCode().is5xxServerError()) {
                log.error("Server error {}", e.getResponseBodyAsString());
                throw new LoanTechException("Server error: " + e.getResponseBodyAsString());
            } else {
                log.error("Unexpected error: {}", e.getResponseBodyAsString());
                throw new LoanTechException("Unexpected error: " + e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            log.error("An unexpected error occurred: ", e);
            throw new LoanTechException("An unexpected error occurred: " + e);
        }
    }

    @Override
    public Loan getLoanDetails(String email) {
        User user = fetchUserDetails(email);

        return fetchLoanDetailsWith(user.getId());
    }

    @Override
    public Loan updateLoanStatus(UpdateLoanStatusRequest request) {
        Loan loan = getLoan(request.getLoanId());
        switch (request.getLoanStatus()){
            case APPROVED -> loan = approveLoan(loan);
            case REJECTED -> loan = rejectLoan(loan);
            case REPAID ->  loan = setLoanToRepaid(loan);
            case DEFAULTED -> loan = setLoanToDefaulted(loan);
        }
        return loan;
    }

    private Loan approveLoan(Loan loan){
        loan.setLoanStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDateTime.now());
        log.info("Loan with id: {} approved on {}", loan.getId(), loan.getApprovalDate());
        return loanRepository.save(loan);
    }

    private Loan rejectLoan(Loan loan){
        loan.setLoanStatus(REJECTED);
        log.info("Loan with id: {} rejected on {}", loan.getId(), LocalDateTime.now());
        return loanRepository.save(loan);
    }
    private Loan setLoanToRepaid(Loan loan){
        loan.setLoanStatus(REPAID);
        log.info("Loan with id: {} repaid on {}", loan.getId(), LocalDateTime.now());
        return loanRepository.save(loan);
    }
    private Loan setLoanToDefaulted(Loan loan){
        loan.setLoanStatus(DEFAULTED);
        log.info("Loan with id: {} defaulted on {}", loan.getId(), LocalDateTime.now());
        return loanRepository.save(loan);
    }

    private Loan getLoan(String loanId){
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException(String.format("Loan with id: %s not found", loanId)));
    }
    private Loan fetchLoanDetailsWith(String userId) {
        return loanRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Loan with user provided does not exist"));
    }

    private User fetchUserDetails(String email) {
        return getUser(email);
    }

    private Loan saveLoanDetails(LoanDetails loanDetails, String userId) {
        Loan loan = modelMapper.map(loanDetails, Loan.class);
        loan.setLoanStatus(LoanStatus.PENDING);
        loan.setUserId(userId);
        loan.setApplicationDate(LocalDateTime.now());
        return loanRepository.save(loan);
    }

    private LoanApplicationResponse mapToLoanApplicationResponse(Loan loan, User user) {
        LoanApplicationResponse response = modelMapper.map(loan, LoanApplicationResponse.class);
        response.setFullName(user.getFirstName() +" "+ user.getLastName());
        return response;
    }


    private String getServiceUrl(String serviceName) {
        return discoveryClient.getInstances(serviceName)
                .stream()
                .findFirst()
                .map(instance -> instance.getUri().toString())
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }


    private void validateEmail(String email) {
        if (!isValidEmail(email))
            throw new InvalidDetailsException(String.format("The email is not a valid one: %s", email));
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber))
            throw new InvalidDetailsException(String.format("The phone number is not a valid one: %s", phoneNumber));
    }

    private void validateDOB(LocalDate dob) {
        LocalDate today = LocalDate.now();
        Period age = Period.between(dob, today);

        if (age.getYears() < 18) {
            throw new InvalidDetailsException("You must be at least 18 years old.");
        }
    }

    void validateDetails(String email, LocalDate dob, String phoneNumber) {
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validateDOB(dob);
    }


}
