package com.eyesmoons.lineage.exception;

public class CustomException extends RuntimeException {

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String format, Object... args) {
        super(String.format(format, args));
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}
