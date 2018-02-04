package com.capgemini.setrack.utility;

import com.capgemini.setrack.exception.InvalidModelException;
import com.capgemini.setrack.model.enums.ConstraintViolations;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationUtility {

    public static InvalidModelException getInvalidModelException(DataIntegrityViolationException e) {
        String errorMessage = e.getMostSpecificCause().getMessage();
        if(errorMessage.contains("Duplicate entry")){
            final Pattern pattern = Pattern.compile("UK_(.+?)\\'");
            final Matcher matcher = pattern.matcher(errorMessage);
            matcher.find();
            return new InvalidModelException(ConstraintViolations.valueOf(matcher.group(1)).getMessage());
        } else if (errorMessage.contains("foreign key constraint")) {
            final Pattern pattern = Pattern.compile("FK_(.+?)\\`");
            final Matcher matcher = pattern.matcher(errorMessage);
            matcher.find();
            return new InvalidModelException(ConstraintViolations.valueOf(matcher.group(1)).getMessage());
        }
        return new InvalidModelException("Something went wrong!");
    }
}
