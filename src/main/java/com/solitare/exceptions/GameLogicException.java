package com.solitare.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class GameLogicException extends RuntimeException {
    public GameLogicException(String msg){
        super(msg);
    }
}