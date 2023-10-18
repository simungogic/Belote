package com.game.belote.exception;

import com.game.belote.response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PlayerNameExistsExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<GenericResponse> playerNameExistsExceptionRes(PlayerNameExistsException playerNameExistsException) {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setCode(HttpStatus.CONFLICT.value());
        genericResponse.setMessage(playerNameExistsException.getMessage());
        genericResponse.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(genericResponse, HttpStatus.CONFLICT);
    }
}
