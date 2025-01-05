package org.simbrella.loanservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.simbrella.loanservice.models.LoanStatus.*;

@ExtendWith(MockitoExtension.class) // Use MockitoJUnit
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private WebClient webClient;

    @Mock
    private DiscoveryClient discoveryClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private LoanServiceImpl loanService;
    private ServiceInstance serviceInstance;

    private UUID userId;
    private UUID loanId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        loanId = UUID.randomUUID();
         serviceInstance = new ServiceInstance() {
            @Override
            public String getServiceId() {
                return "user-service";
            }

            @Override
            public URI getUri() {
                return URI.create("http://user-service");
            }

            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public int getPort() {
                return 8080;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public Map<String, String> getMetadata() {
                return Collections.emptyMap();
            }
        };
    }

    @Test
    void testGetInstances() {
        // Create a custom implementation of ServiceInstance
        ServiceInstance serviceInstance = new ServiceInstance() {
            @Override
            public String getServiceId() {
                return "user-service";
            }

            @Override
            public URI getUri() {
                return URI.create("http://user-service");
            }

            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public int getPort() {
                return 8080;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public Map<String, String> getMetadata() {
                return Collections.emptyMap();
            }
        };

        when(discoveryClient.getInstances(anyString())).thenReturn(List.of(serviceInstance));

        List<ServiceInstance> instances = discoveryClient.getInstances("user-service");

        verify(discoveryClient).getInstances("user-service");
        assertEquals("http://user-service", instances.get(0).getUri().toString());
    }

    @Test
    void applyForLoan_validRequest_returnsLoanApplicationResponse() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setEmail("test@example.com");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setPhoneNumber("1234567890");
        request.setLoanAmount(BigDecimal.valueOf(5000));
        request.setLoanTermMonths(12);
        request.setAnnualIncome(BigDecimal.valueOf(60000));

        User user = new User();
        user.setId(userId.toString());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");

        LoanApplicationResponse loanApplicationResponse = new LoanApplicationResponse();
        loanApplicationResponse.setFullName("John Doe");
        loanApplicationResponse.setLoanStatus(PENDING);

        Loan loan = new Loan();
        loan.setId(loanId.toString());
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setInterestRate(BigDecimal.valueOf(5.0));
        loan.setApplicationDate(LocalDateTime.now());
        loan.setLoanStatus(PENDING);
        loan.setUserId(userId.toString());
        loan.setMonthlyInstallment(BigDecimal.valueOf(5500));
        loan.setLoanTermMonths(12);
        loan.setTotalInterest(BigDecimal.valueOf(500));

        when(discoveryClient.getInstances(anyString())).thenReturn(List.of(serviceInstance));
        when(webClient.get().uri(anyString(), anyString()).retrieve().bodyToMono(User.class).block()).thenReturn(user);

        when(loanService.getUser(anyString())).thenReturn(user);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(modelMapper.map(any(), any())).thenReturn(loanApplicationResponse);

         // Mock the WebClient flow
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(User.class)).thenReturn(Mono.just(user));

        LoanApplicationResponse response = loanService.applyForLoan(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("PENDING", response.getLoanStatus());
    }

    @Test
    void applyForLoan_invalidEmail_throwsInvalidDetailsException() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setEmail("invalid-email");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setPhoneNumber("1234567890");
        request.setLoanAmount(BigDecimal.valueOf(5000));
        request.setLoanTermMonths(12);
        request.setAnnualIncome(BigDecimal.valueOf(60000));

        InvalidDetailsException exception = assertThrows(InvalidDetailsException.class, () -> loanService.applyForLoan(request));
        assertEquals("The email is not a valid one: invalid-email", exception.getMessage());
    }

    @Test
    void getUser_validEmail_returnsUser() {
        ServiceInstance serviceInstance = new ServiceInstance() {
            @Override
            public String getServiceId() {
                return "user-service";
            }

            @Override
            public URI getUri() {
                return URI.create("http://user-service");
            }

            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public int getPort() {
                return 8080;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public Map<String, String> getMetadata() {
                return Collections.emptyMap();
            }
        };

        User user = new User();
        user.setId(userId.toString());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");

        when(discoveryClient.getInstances(anyString())).thenReturn(List.of(serviceInstance));
        when(webClient.get().uri(anyString(), anyString()).retrieve().bodyToMono(User.class).block()).thenReturn(user);

        User fetchedUser = loanService.getUser("test@example.com");

        assertNotNull(fetchedUser);
        assertEquals("test@example.com", fetchedUser.getEmail());
    }

    @Test
    void getUser_userNotFound_throwsNotFoundException() {
        when(webClient.get().uri(anyString(), anyString()).retrieve().bodyToMono(User.class).block()).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> loanService.getUser("notfound@example.com"));
    }

    @Test
    void getLoanDetails_validEmail_returnsLoan() {
        User user = new User();
        user.setId(userId.toString());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");

        Loan loan = new Loan();
        loan.setId(loanId.toString());
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setInterestRate(BigDecimal.valueOf(5.0));
        loan.setApplicationDate(LocalDateTime.now());
        loan.setLoanStatus(PENDING);
        loan.setUserId(userId.toString());
        loan.setMonthlyInstallment(BigDecimal.valueOf(5500));
        loan.setLoanTermMonths(12);
        loan.setTotalInterest(BigDecimal.valueOf(500));

        when(loanService.getUser(anyString())).thenReturn(user);
        when(loanRepository.findByUserId(anyString())).thenReturn(Optional.of(loan));

        Loan fetchedLoan = loanService.getLoanDetails("test@example.com");

        assertNotNull(fetchedLoan);
        assertEquals(loanId.toString(), fetchedLoan.getId());
    }

    @Test
    void updateLoanStatus_approveLoan_updatesLoanStatus() {
        Loan loan = new Loan();
        loan.setId(loanId.toString());
        loan.setLoanAmount(BigDecimal.valueOf(5000));
        loan.setInterestRate(BigDecimal.valueOf(5.0));
        loan.setApplicationDate(LocalDateTime.now());
        loan.setLoanStatus(PENDING);
        loan.setUserId(userId.toString());
        loan.setMonthlyInstallment(BigDecimal.valueOf(5500));
        loan.setLoanTermMonths(12);
        loan.setTotalInterest(BigDecimal.valueOf(500));

        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setLoanId(loanId.toString());
        request.setLoanStatus(APPROVED);

        when(loanRepository.findById(anyString())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan updatedLoan = loanService.updateLoanStatus(request);

        assertNotNull(updatedLoan);
        assertEquals(APPROVED, updatedLoan.getLoanStatus());
    }

    @Test
    void updateLoanStatus_loanNotFound_throwsNotFoundException() {
        when(loanRepository.findById(anyString())).thenReturn(Optional.empty());

        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setLoanId(loanId.toString());
        request.setLoanStatus(APPROVED);

        assertThrows(NotFoundException.class, () -> loanService.updateLoanStatus(request));
    }

    @Test
    void validateDetails_validDetails_doesNotThrow() {
        assertDoesNotThrow(() -> loanService.validateDetails("test@example.com", LocalDate.of(1990, 1, 1), "1234567890"));
    }

    @Test
    void validateDetails_invalidDOB_throwsInvalidDetailsException() {
        LocalDate dob = LocalDate.now().minusYears(17);

        InvalidDetailsException exception = assertThrows(InvalidDetailsException.class, () -> loanService.validateDetails("test@example.com", dob, "1234567890"));
        assertEquals("You must be at least 18 years old.", exception.getMessage());
    }
}