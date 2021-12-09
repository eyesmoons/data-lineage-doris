package com.eyesmoons.lineage.exception;

/**
 * 解析异常处理
 */
public class ParserException extends RuntimeException {

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String format, Object... args) {
        super(String.format(format, args));
    }

    public ParserException(Throwable cause) {
        super(cause);
    }
}
