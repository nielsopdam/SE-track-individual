package com.capgemini.setrack.utility;

import com.capgemini.setrack.exception.InvalidModelException;
import com.capgemini.setrack.model.enums.ConstraintViolations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationUtility {

    public static InvalidModelException getInvalidModelException(DataIntegrityViolationException e) {
        String errorMessage = e.getMostSpecificCause().getMessage();
        System.out.println(errorMessage);
        if(errorMessage.contains("Duplicate entry")){
            final Pattern pattern = Pattern.compile("UK_(.+?)\\'");
            final Matcher matcher = pattern.matcher(errorMessage);
            matcher.find();
            return new InvalidModelException(ConstraintViolations.valueOf(matcher.group(1)).getMessage());
        } else if (errorMessage.contains("foreign key constraint")) {
            System.out.println("Appelsap");
            final Pattern pattern = Pattern.compile("FK_(.+?)\\`");
            final Matcher matcher = pattern.matcher(errorMessage);
            matcher.find();
            return new InvalidModelException(ConstraintViolations.valueOf(matcher.group(1)).getMessage());
        }
        return new InvalidModelException("Something went wrong!");
    }
}
