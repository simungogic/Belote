package com.game.belote.exception;

import com.game.belote.response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NotEnoughPlayersExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<GenericResponse> notEnoughPlayersExceptionRes(NotEnoughPlayersException notEnoughPlayersException) {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setCode(HttpStatus.LOCKED.value());
        genericResponse.setMessage(notEnoughPlayersException.getMessage());
        genericResponse.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(genericResponse, HttpStatus.LOCKED);
    }
}
