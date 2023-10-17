package com.game.belote.response;

import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
public class GenericResponse {
    private int code;
    private String message;
    private long timestamp;
}
