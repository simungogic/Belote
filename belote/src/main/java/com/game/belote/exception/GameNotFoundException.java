package com.game.belote.exception;

public class GameNotFoundException extends RuntimeException {
    private String message;
    public GameNotFoundException(String message) {
        super(message);
    }
}
