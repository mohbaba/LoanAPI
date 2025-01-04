package org.simbrella.loanservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.UUID;
import static java.time.LocalDateTime.now;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private LoanStatus loanStatus;
    private String userId;
    private BigDecimal monthlyInstallment;
    private Integer loanTermMonths;
    private BigDecimal totalInterest;

    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeCreated;

    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeUpdated;


    @PrePersist
    private void setTimeCreated() {
        timeCreated = now();
    }

    @PreUpdate
    private void setTimeUpdated() {
        timeUpdated = now();
    }


}
