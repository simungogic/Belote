package com.game.belote.exception;

public class NotEnoughPlayersException extends RuntimeException {
    private String message;
    public NotEnoughPlayersException(String message) {
        super(message);
    }
}
