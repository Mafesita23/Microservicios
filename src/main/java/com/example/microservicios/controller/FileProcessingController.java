package com.example.microservicios.controller;

import com.example.microservicios.validation.ValidationService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api/file")
public class FileProcessingController {

    @Autowired
    private ValidationService validationService;

    @PostMapping("/UploadCSV")
    public ResponseEntity<List<String>> processCSVFile(@RequestParam("file") MultipartFile file) {
        try {
            // Verifica que el archivo no esté vacío
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            // Abre un flujo de entrada desde el archivo
            InputStream inputStream = file.getInputStream();

            // Crea un analizador CSV
            InputStreamReader reader = new InputStreamReader(inputStream);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

            // Obtiene registros CSV y realiza la validación
            List<String> validLines = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                // Realiza la validación de la línea
                if (validationService.validateCSVRecord(csvRecord)) {
                    validLines.add(csvRecord.toString());
                }
            }

            // Cierra el flujo de entrada
            inputStream.close();

            return ResponseEntity.ok(validLines);
        } catch (Exception e) {
            // Manejo de errores
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/UploadExcel")
    public ResponseEntity<List<String>> processExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            // Verifica que el archivo no esté vacío
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            // Abre un flujo de entrada desde el archivo
            InputStream inputStream = file.getInputStream();

            // Crea un libro de trabajo (workbook) a partir del archivo Excel
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Obtiene la hoja (sheet) del libro de trabajo (puedes ajustar el índice según tus necesidades)
            Sheet sheet = workbook.getSheetAt(0);

            // Define el rango de filas que deseas validar
            int startRow = 1; // La primera fila generalmente contiene encabezados
            int endRow = sheet.getLastRowNum();

            // Obtiene registros Excel y realiza la validación
            List<String> validLines = new ArrayList<>();
            for (int i = startRow; i <= endRow; i++) {
                Row row = sheet.getRow(i);

                // Realiza la validación de la fila
                if (validationService.validateExcelFile(file)){
                    // Construye una representación de cadena de la fila
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        line.append(row.getCell(j).toString()).append(",");
                    }
                    line.deleteCharAt(line.length() - 1); // Elimina la última coma

                    validLines.add(line.toString());
                }
            }

            // Cierra el flujo de entrada
            inputStream.close();

            return ResponseEntity.ok(validLines);
        } catch (Exception e) {
            // Manejo de errores
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Otros métodos y rutas del controlador
}