package com.game.belote.exception;

import com.game.belote.response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NoDealerExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<GenericResponse> noDealerExceptionRes(NoDealerException noDealerException) {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setCode(HttpStatus.NOT_ACCEPTABLE.value());
        genericResponse.setMessage(noDealerException.getMessage());
        genericResponse.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(genericResponse, HttpStatus.NOT_ACCEPTABLE);
    }
}
