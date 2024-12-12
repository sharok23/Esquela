package com.sharok.esquela.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
