package com.example.microservicios.service;

import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVRecord;

@Service
public class ValidationService {

    public boolean validateCSVRecord(CSVRecord record) {
        // Validar el campo de correo electrónico
        String email = record.get("email");
        if (!isValidEmail(email)) {
            return false;
        }

        // Validar el campo de fecha de nacimiento
        String dateOfBirth = record.get("dateOfBirth");
        if (!isValidDateOfBirth(dateOfBirth)) {
            return false;
        }

        // Validar el campo de título de trabajo
        String jobTitle = record.get("jobTitle");
        if (!isValidJobTitle(jobTitle)) {
            return false;
        }

        // Todas las validaciones pasaron, el registro es válido
        return true;
    }

    public boolean isValidEmail(String email) {
        // Validar el campo de correo electrónico con una expresión regular
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    public boolean isValidDateOfBirth(String dateOfBirth) {
        try {
            // Validar el campo de fecha de nacimiento y verificar si es mayor a 1980-01-01
            java.util.Date birthDate = java.sql.Date.valueOf(dateOfBirth);
            java.util.Date referenceDate = java.sql.Date.valueOf("1980-01-01");
            return birthDate.compareTo(referenceDate) > 0;
        } catch (IllegalArgumentException e) {
            // Si no se puede analizar la fecha, es inválida
            return false;
        }
    }

    public boolean isValidJobTitle(String jobTitle) {
        // Validar el campo de título de trabajo
        String[] validTitles = {
                "Haematologist", "Phytotherapist", "Building surveyor",
                "Insurance account manager", "Educational psychologist"
        };
        for (String validTitle : validTitles) {
            if (validTitle.equalsIgnoreCase(jobTitle)) {
                return true;
            }
        }
        return false; // Si no coincide con ninguno de los títulos válidos
    }
}
