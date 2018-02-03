package com.capgemini.setrack.exception;

import com.capgemini.setrack.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import java.util.Set;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidModelException extends ResponseException {

    public InvalidModelException() {}

    public InvalidModelException(String message){
        super(message);
    }

    public InvalidModelException(Set<ConstraintViolation<Model>> violations){
        super(violations);
    }
}