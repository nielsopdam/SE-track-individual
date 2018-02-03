package com.capgemini.setrack.exception;

import com.capgemini.setrack.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ResponseException extends Exception {

    private String message;

    ResponseException(){}

    ResponseException(Set<ConstraintViolation<Model>> violations){
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<Model> cv : violations) {
            errors.add(cv.getMessage());
        }
        this.message = String.join(",<br>", errors);
    }

    ResponseException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }

}
