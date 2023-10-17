package com.game.belote.exception;

import com.game.belote.response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GameFullExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<GenericResponse> gameFullExceptionRes(GameFullException gameFullException) {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setCode(HttpStatus.FORBIDDEN.value());
        genericResponse.setMessage(gameFullException.getMessage());
        genericResponse.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(genericResponse, HttpStatus.FORBIDDEN);
    }
}
