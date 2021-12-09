package com.eyesmoons.lineage.exception;

/**
 * JSON异常
 */
public class JsonException extends RuntimeException {

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String format, Object... args) {
        super(String.format(format, args));
    }

    public JsonException(Throwable cause) {
        super(cause);
    }
}
