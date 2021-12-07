package com.eyesmoons.lineage.common.exception;

/**
 * JSON异常
 */
public class CommonException extends RuntimeException {

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(String format, Object... args) {
        super(String.format(format, args));
    }

    public CommonException(Throwable cause) {
        super(cause);
    }
}
