package com.example.scheduledemo.exception;

import com.example.scheduledemo.api.vo.APIResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<APIResultVO<Void>> handleBusinessExceptions(BusinessException ex) {
        log.error("{}", ex.getMessage());
        APIResultVO<Void> errorResponse = new APIResultVO<>(500L, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResultVO<Void>> handleAllExceptions(Exception ex) {
        log.error("{}", ex.getMessage());
        APIResultVO<Void> errorResponse = new APIResultVO<>(500L, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}