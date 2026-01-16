package com.markdowntoword.exception;

/**
 * Exception thrown when an error occurs during Markdown parsing.
 */
public class MarkdownParseException extends RuntimeException {

    /**
     * Constructs a new MarkdownParseException with the specified detail message.
     *
     * @param message the detail message
     */
    public MarkdownParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new MarkdownParseException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public MarkdownParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
