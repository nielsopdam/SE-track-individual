package com.capgemini.setrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends ResponseException {

    public NotFoundException() {}

    public NotFoundException(String message){
        super(message);
    }
}