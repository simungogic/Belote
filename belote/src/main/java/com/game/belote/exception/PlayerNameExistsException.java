package com.game.belote.exception;

public class PlayerNameExistsException extends RuntimeException {
    private String message;

    public PlayerNameExistsException(String message) {
        super(message);
    }
}
