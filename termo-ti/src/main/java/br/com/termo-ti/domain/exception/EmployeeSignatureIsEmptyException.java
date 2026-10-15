package br.com.TermoTI.domain.exception;

public class EmployeeSignatureIsEmptyException extends RuntimeException {

    public EmployeeSignatureIsEmptyException(String message) {
        super(message);
    }

}
