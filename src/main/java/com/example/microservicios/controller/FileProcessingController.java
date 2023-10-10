package com.example.microservicios.controller;
import com.example.microservicios.validation.ValidationResult;
import com.example.microservicios.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileProcessingController {
    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping("/process")
    public ResponseEntity<?> processFile(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");
        ValidationResult result = fileProcessingService.processFile(filePath);
        return ResponseEntity.ok(result);
    }
}
