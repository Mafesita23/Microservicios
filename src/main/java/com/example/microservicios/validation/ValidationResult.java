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

    public ValidationResult merge(boolean otherResult) {
        if (!otherResult) {
            errorMessages.add("Operación no válida.");
        }
        return this;
    }

    public boolean isValid() {
        return errorMessages.isEmpty();
    }

    public ValidationResult merge(ValidationResult otherResult) {
        errorMessages.addAll(otherResult.getErrorMessages());
        return this;
    }
}


