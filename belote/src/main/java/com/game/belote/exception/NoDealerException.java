package com.game.belote.exception;

public class NoDealerException extends RuntimeException {
    private String message;

    public NoDealerException(String message) {
        super(message);
    }
}
