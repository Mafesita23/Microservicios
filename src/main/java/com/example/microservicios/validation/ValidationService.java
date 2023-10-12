package com.example.microservicios.validation;

import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ValidationService {

    public boolean validateCSVRecord(CSVRecord record) {
        ValidationResult result = new ValidationResult();

        // Validar el campo de correo electrónico
        String email = record.get(0);  // Accede por índice (0 para la primera columna)
        if (!isValidEmail(email)) {
            result.addErrorMessage("Correo electrónico inválido en línea: " + record.getRecordNumber());
        }

        // Validar el campo de fecha de nacimiento
        String dateOfBirth = record.get(1);  // Accede por índice (1 para la segunda columna)
        if (!isValidDateOfBirth(dateOfBirth)) {
            result.addErrorMessage("Fecha de nacimiento inválida en línea: " + record.getRecordNumber());
        }

        // Validar el campo de título de trabajo
        String jobTitle = record.get(2);  // Accede por índice (2 para la tercera columna)
        if (!isValidJobTitle(jobTitle)) {
            result.addErrorMessage("Título de trabajo inválido en línea: " + record.getRecordNumber());
        }

        return result.isValid();
    }

    public ValidationResult validateTXTLine(String line) {
        ValidationResult result = new ValidationResult();

        // Validar la línea de archivo TXT aquí
        if (line == null || line.isEmpty()) {
            result.addErrorMessage("La línea está vacía o es nula");
        } else {
            // Ejemplo de validación simple: Verificar si la línea contiene al menos 10 caracteres
            if (line.length() < 10) {
                result.addErrorMessage("La línea debe contener al menos 10 caracteres");
            }

            // Puedes agregar más lógica de validación según tus necesidades
        }

        return result;
    }

    public boolean validateExcelFile(MultipartFile file) {
        ValidationResult result = new ValidationResult();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Agrega tus validaciones de archivo Excel aquí según los requisitos de tu aplicación.
            // Puedes verificar la estructura del archivo, número de hojas, formatos, etc.

        } catch (IOException e) {
            e.printStackTrace();
            result.addErrorMessage("Error al leer el archivo Excel");
        }

        return result.isValid();
    }

    public ValidationResult validateExcelData(String filePath, int sheetIndex, int rowIndex) {
        ValidationResult result = new ValidationResult();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Row row = sheet.getRow(rowIndex);

            // Validar los campos de la fila del archivo Excel
            Cell emailCell = row.getCell(0);
            Cell dateOfBirthCell = row.getCell(1);
            Cell jobTitleCell = row.getCell(2);

            String email = emailCell.getStringCellValue();
            String dateOfBirth = dateOfBirthCell.getStringCellValue();
            String jobTitle = jobTitleCell.getStringCellValue();

            if (!isValidEmail(email)) {
                result.addErrorMessage("Correo electrónico inválido en fila: " + (rowIndex + 1));
            }

            if (!isValidDateOfBirth(dateOfBirth)) {
                result.addErrorMessage("Fecha de nacimiento inválida en fila: " + (rowIndex + 1));
            }

            if (!isValidJobTitle(jobTitle)) {
                result.addErrorMessage("Título de trabajo inválido en fila: " + (rowIndex + 1));
            }

        } catch (IOException e) {
            e.printStackTrace();
            result.addErrorMessage("Error al leer el archivo Excel");
        }

        return result;
    }

    public ValidationResult validateXLSXRow(Row row, Row headerRow) {
        ValidationResult result = new ValidationResult();

        // Obtener el índice de las columnas en función de los encabezados en headerRow
        int emailColumnIndex = getColumnIndex(headerRow, "Email");
        int dateOfBirthColumnIndex = getColumnIndex(headerRow, "Date of birth");
        int jobTitleColumnIndex = getColumnIndex(headerRow, "Job Title");

        if (emailColumnIndex < 0 || dateOfBirthColumnIndex < 0 || jobTitleColumnIndex < 0) {
            result.addErrorMessage("Columnas requeridas no encontradas en el encabezado");
        } else {
            // Validar los campos de la fila del archivo Excel
            Cell emailCell = row.getCell(emailColumnIndex);
            Cell dateOfBirthCell = row.getCell(dateOfBirthColumnIndex);
            Cell jobTitleCell = row.getCell(jobTitleColumnIndex);

            String email = emailCell.getStringCellValue();
            String dateOfBirth = dateOfBirthCell.getStringCellValue();
            String jobTitle = jobTitleCell.getStringCellValue();

            if (!isValidEmail(email)) {
                result.addErrorMessage("Correo electrónico inválido en la fila: " + (row.getRowNum() + 1));
            }

            if (!isValidDateOfBirth(dateOfBirth)) {
                result.addErrorMessage("Fecha de nacimiento inválida en la fila: " + (row.getRowNum() + 1));
            }

            if (!isValidJobTitle(jobTitle)) {
                result.addErrorMessage("Título de trabajo inválido en la fila: " + (row.getRowNum() + 1));
            }
        }

        return result;
    }

    private boolean isValidEmail(String email) {
        // Implementar la validación de correo electrónico con una expresión regular
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidDateOfBirth(String dateOfBirth) {
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

    private boolean isValidJobTitle(String jobTitle) {
        // Implementar la validación de título de trabajo
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

    // Método para encontrar el índice de la columna en función del encabezado
    private int getColumnIndex(Row headerRow, String header) {
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().equalsIgnoreCase(header)) {
                return cell.getColumnIndex();
            }
        }
        return -1; // Devuelve -1 si no se encuentra la columna
    }
}
