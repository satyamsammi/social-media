package com.satyamsammi.socialmedia.exceptions;

import com.satyamsammi.socialmedia.dtos.genericresponse.FailureResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        FailureResponse response = FailureResponse.builder()
                .success(false)
                .error(status.toString())
                .build();
        return new ResponseEntity<>(response, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        FailureResponse response = FailureResponse.builder()
                .success(false)
                .error(String.join(",", errors))
                .build();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(APIRuntimeException.class)
    public ResponseEntity<FailureResponse> handleApiException(APIRuntimeException ex) {
        FailureResponse response = FailureResponse.builder()
                .success(false)
                .error(ex.getMessage())
                .build();
        return new ResponseEntity<FailureResponse>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailureResponse> handleAllExceptions(Exception ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : ex.toString();
        FailureResponse response = FailureResponse.builder()
                .success(false)
                .error(message)
                .build();
        return new ResponseEntity<FailureResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
