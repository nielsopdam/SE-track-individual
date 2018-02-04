package com.capgemini.setrack.utility;

import com.capgemini.setrack.exception.InvalidModelException;
import com.capgemini.setrack.model.enums.ConstraintViolations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class which helps interpret unclear exception messages thrown by (for example) the database.
 * Makes sure to return clear messages that can directly be returned by the API.
 */

public final class ValidationUtility {

    /**
     * Parses messages from DataIntegrityViolationExceptions and instead returns clear messages
     * via an InvalidModelException.
     *
     * @param e The error to interpret the message from.
     *
     * @return The clear message for the user of the API.
     */
    public static InvalidModelException getInvalidModelException(DataIntegrityViolationException e) {
        String errorMessage = e.getMostSpecificCause().getMessage();

        //TODO Find out of there actually is a way to directly retrieve constraint name from error
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
