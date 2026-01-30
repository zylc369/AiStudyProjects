package com.markdown.toword.model;

/**
 * Exception thrown when Markdown to Word conversion fails.
 */
public class ConversionException extends Exception {

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
     * @param cause the cause of the exception
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
