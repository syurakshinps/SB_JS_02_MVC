package org.example.app.exceptions;

public class NoFilenameGivenException extends Exception {
    private final String message;
    public NoFilenameGivenException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
