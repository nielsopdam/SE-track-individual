package com.capgemini.setrack.model;

import com.capgemini.setrack.exception.InvalidModelException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class Model {

    public void validate() throws InvalidModelException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Model>> violations = validator.validate(this);

        if(violations.size() > 0){
            throw new InvalidModelException(violations);
        }
    }
}
