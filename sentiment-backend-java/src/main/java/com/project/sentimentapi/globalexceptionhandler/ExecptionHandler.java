package com.project.sentimentapi.globalexceptionhandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class ExecptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HashMap<String, List<String>>> Exeption(ConstraintViolationException c) {
        HashMap<String, List<String>> map = new HashMap<>();
        List<String> valor = new ArrayList<>();
        for (ConstraintViolation<?> constraint : c.getConstraintViolations()) {
            valor.add(constraint.getMessage());
        }
        map.put("Error", valor);
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
