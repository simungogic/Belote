package com.game.belote.exception;

public class GameFullException extends RuntimeException {
    private String message;

    public GameFullException(String message) {
        super(message);
    }
}
