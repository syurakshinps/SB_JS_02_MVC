package org.example.app.exceptions;

public class BookShelfRegexException extends Exception {
    private final String message;
    public BookShelfRegexException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
