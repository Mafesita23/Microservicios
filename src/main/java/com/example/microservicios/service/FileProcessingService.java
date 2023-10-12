package com.example.microservicios.service;

import com.example.microservicios.validation.ValidationResult;
import com.example.microservicios.validation.ValidationService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Service
public class FileProcessingService {

    @Autowired
    private ValidationService validationService;

    public ValidationResult processFile(MultipartFile file) {
        ValidationResult result = new ValidationResult();

        try {
            // Obtén el nombre del archivo original
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                if (fileName.endsWith(".csv")) {
                    processCSVFile(file, result);
                } else if (fileName.endsWith(".xlsx")) {
                    processXLSXFile(file, result);
                } else if (fileName.endsWith(".txt")) {
                    processTXTFile(file, result);
                } else {
                    result.addErrorMessage("Tipo de archivo no admitido");
                }
            } else {
                result.addErrorMessage("Nombre de archivo inválido");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.addErrorMessage("Error al procesar el archivo");
        }

        return result;
    }

    private void processCSVFile(MultipartFile file, ValidationResult result) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                // Ahora puedes acceder a las columnas por nombre
                String index = csvRecord.get("Index");
                String userId = csvRecord.get("User Id");
                String firstName = csvRecord.get("First Name");
                String lastName = csvRecord.get("Last Name");
                String sex = csvRecord.get("Sex");
                String email = csvRecord.get("Email");
                String phone = csvRecord.get("Phone");
                String dateOfBirth = csvRecord.get("Date of birth");
                String jobTitle = csvRecord.get("Job Title");

                // Luego, puedes realizar validaciones u operaciones con estas columnas
            }
        }
    }

    private void processXLSXFile(MultipartFile file, ValidationResult result) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Supongamos que estamos trabajando con la primera hoja del libro

            // Supongamos que la primera fila contiene los nombres de las columnas
            Row headerRow = sheet.getRow(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Saltar la primera fila, que es la fila de encabezado
                    continue;
                }

                boolean recordValidationResult = validationService.validateXLSXRow(row, headerRow).isValid();
                if (!recordValidationResult) {
                    result.merge(recordValidationResult);
                }
            }
        } catch (Exception e) {
            // Manejar excepciones
            e.printStackTrace();
        }
    }

    private void processTXTFile(MultipartFile file, ValidationResult result) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                ValidationResult lineValidationResult = validationService.validateTXTLine(line);
                if (!lineValidationResult.isValid()) {
                    result.addErrorMessage("Error en la línea " + lineNumber + ": " + lineValidationResult.getErrorMessages());
                }
                lineNumber++;
            }
        }
    }
}

