package com.example.microservicios.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private List<String> errorMessages = new ArrayList<>();

    public ValidationResult() {
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public ValidationResult addErrorMessage(String errorMessage) {
        errorMessages.add(errorMessage);
        return this;
    }

    public ValidationResult merge(ValidationResult otherResult) {
        errorMessages.addAll(otherResult.getErrorMessages());
        return this;
    }

    public boolean isValid() {
        return errorMessages.isEmpty();
    }

    public static ValidationResult mergeAll(List<ValidationResult> results) {
        return results.stream()
                .reduce(new ValidationResult(), ValidationResult::merge);
    }
}


