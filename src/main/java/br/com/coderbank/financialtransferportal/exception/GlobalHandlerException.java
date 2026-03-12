package br.com.coderbank.financialtransferportal.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e){

        Map<String, String> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1
                ));

        var problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );

        problemDetail.setTitle("Invalid request data");
        problemDetail.setProperty("errors", errors);
        problemDetail.setType(URI.create("https://api.coderbank.com.br"));


        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidJson(HttpMessageNotReadableException e){

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Invalid Json");
        problemDetail.setDetail("Request body is malformed");
        problemDetail.setType(URI.create("https://api.coderbank.com.br"));

        return problemDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(EntityNotFoundException e){

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problemDetail.setTitle("Resource not found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://api.coderbank.com.br/erros"));

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGeneric(Exception e){

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problem.setTitle("Internal server error");
        problem.setDetail("Unexpected error occurred");

        return problem;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ProblemDetail handleInsufficientBalanceException(InsufficientBalanceException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.coderbank.com.br/erros/saldo-insuficiente"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }
}
