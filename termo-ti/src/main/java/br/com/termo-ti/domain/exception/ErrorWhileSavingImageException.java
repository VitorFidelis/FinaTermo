package br.com.TermoTI.domain.exception;

import java.io.IOException;

public class ErrorWhileSavingImageException extends RuntimeException {

    public ErrorWhileSavingImageException(String message, IOException exception) {
        super(message, exception);
    }

}
