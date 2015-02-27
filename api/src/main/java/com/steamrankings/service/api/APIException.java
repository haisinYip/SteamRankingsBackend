package com.steamrankings.service.api;

public class APIException extends Exception {
    private static final long serialVersionUID = 1L;

    // Constructor that accepts a message
    public APIException(String message) {
        super(message);
    }
}