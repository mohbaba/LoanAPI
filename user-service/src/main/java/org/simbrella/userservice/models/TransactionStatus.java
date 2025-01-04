package org.simbrella.userservice.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionStatus {
    SUCCESS("success"),
    REVERSED("reversed"),
    QUEUED("queued"),
    ABANDONED("abandoned"),
    PENDING("pending"),
    PROCESSING("processing"),
    ONGOING("ongoing"),
    FAILED("failed");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static TransactionStatus fromString(String status) {
        for (TransactionStatus ts : TransactionStatus.values()) {
            if (ts.value.equalsIgnoreCase(status)) {
                return ts;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}

