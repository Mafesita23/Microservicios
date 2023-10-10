package com.example.microservicios.service;

import com.example.microservicios.validation.ValidationResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Service
public class FileProcessingService {

    @Autowired
    private ValidationService validationService;

    public ValidationResult processFile(String filePath) {
        ValidationResult result = new ValidationResult();

        try {
            // Determine el tipo de archivo según la extensión (CSV, Excel o TXT)
            if (filePath.endsWith(".csv")) {
                processCSVFile(filePath, result);
            } else if (filePath.endsWith(".xlsx")) {
                // Lógica para procesar archivos Excel (igual que antes)
            } else if (filePath.endsWith(".txt")) {
                processTXTFile(filePath, result);
            } else {
                result.addErrorMessage("Tipo de archivo no admitido");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.addErrorMessage("Error al procesar el archivo");
        }

        return result;
    }

    private void processCSVFile(String filePath, ValidationResult result) throws IOException {
        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord csvRecord : csvParser) {
                // Procesa cada línea del archivo CSV
                ValidationResult recordValidationResult = validationService.validateCSVRecord(csvRecord);
                result.merge(recordValidationResult);
            }
        }
    }

    private void processTXTFile(String filePath, ValidationResult result) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                // Procesa cada línea del archivo de texto (TXT)
                ValidationResult lineValidationResult = validationService.validateTXTLine(line);
                if (!lineValidationResult.isValid()) {
                    result.addErrorMessage("Error en la línea " + lineNumber + ": " + lineValidationResult.getErrorMessages());
                }
            }
        }
    }
}
