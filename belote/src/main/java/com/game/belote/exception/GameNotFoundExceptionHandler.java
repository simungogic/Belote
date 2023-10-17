package com.game.belote.exception;

import com.game.belote.response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GameNotFoundExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<GenericResponse> gameNotFoundExceptionRes(GameNotFoundException gameNotFoundException) {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setCode(HttpStatus.NOT_FOUND.value());
        genericResponse.setMessage(gameNotFoundException.getMessage());
        genericResponse.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(genericResponse, HttpStatus.NOT_FOUND);
    }
}
