package br.com.TermoTI.api.handler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.TermoTI.domain.exception.EmployeeSignatureIsEmptyException;
import br.com.TermoTI.domain.exception.ErrorWhileSavingImageException;
import br.com.TermoTI.domain.exception.ResponsibleITSignatureIsEmptyException;
import br.com.TermoTI.domain.exception.TermNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ProblemDetail handleFieldErrors(MethodArgumentNotValidException ex) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage());
                problemDetail.setTitle("The request contains invalid fields.");
                problemDetail.setDetail("One or more fields failed validation. Please correct them and try again.");
                Map<String, String> invalidParams = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .collect(Collectors.toMap(
                                                error -> error.getField(),
                                                error -> error.getDefaultMessage(),
                                                (mensagemExistente, novaMensagem) -> mensagemExistente));
                problemDetail.setProperty("invalid-params", invalidParams);
                return problemDetail;
        }

        @ExceptionHandler(ServerErrorException.class)
        public ProblemDetail handleInternalServerErrors(ServerErrorException ex) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ex.getMessage());
                problemDetail.setTitle("Internal server error");
                problemDetail.setProperty("timestamp", Instant.now());
                return problemDetail;
        }

        @ExceptionHandler(EmployeeSignatureIsEmptyException.class)
        public ProblemDetail handleEmptyEmployeeSignature(EmployeeSignatureIsEmptyException ex) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                ex.getMessage());
                problemDetail.setTitle("Employee Signature Required");
                problemDetail.setProperty("timestamp", Instant.now());
                return problemDetail;
        }

        @ExceptionHandler(ResponsibleITSignatureIsEmptyException.class)
        public ProblemDetail handleResponsibleITEmtptySignature(ResponsibleITSignatureIsEmptyException ex) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                ex.getMessage());
                problemDetail.setTitle("IT Signature Required");
                problemDetail.setProperty("timestamp", Instant.now());
                return problemDetail;
        }

        @ExceptionHandler(ErrorWhileSavingImageException.class)
        public ProblemDetail handleErrorWhileSavingImage(ErrorWhileSavingImageException ex) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ex.getMessage());
                problemDetail.setTitle("Internal Error Saving Image");
                problemDetail.setProperty("timestamp", Instant.now());
                return problemDetail;
        }

        @ExceptionHandler(TermNotFoundException.class)
        public ProblemDetail handleTermNotFound(TermNotFoundException ex) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage());
                problemDetail.setTitle("Term not found");
                problemDetail.setProperty("timestamp", Instant.now());
                return problemDetail;
        }
}
