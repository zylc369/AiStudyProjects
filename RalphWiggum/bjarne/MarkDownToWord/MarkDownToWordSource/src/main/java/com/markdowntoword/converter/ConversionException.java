package com.markdowntoword.converter;

/**
 * Exception thrown when an error occurs during document conversion to Word format.
 */
public class ConversionException extends RuntimeException {

    /**
     * Constructs a new ConversionException with the specified detail message.
     *
     * @param message the detail message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConversionException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
