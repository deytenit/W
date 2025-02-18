package com.ermnvldmr.w.utils;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<String, Object> getValidationErrors(BindingResult bindingResult) {
        Map<String, Object> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return Map.of("validationErrors", errors);  // Structured JSON format
    }
}
