package com.capgemini.setrack.model.enums;

/**
 * A list of violation messages relating to specific key names in the database.
 */
public enum ConstraintViolations {
    AIRPORT_COUNTRY_CITY("This airport already exists!!"),
    AIRPLANE_AIRPLANENUMBER("An airplane with this number already exists!!"),
    AIRPLANE_AIRPORT("This is not an existing airport!"),
    FLIGHT_LIFTOFF("This flight has already been added!"),
    FLIGHT_AIRPLANE("This is not an existing airplane!"),
    FLIGHT_STARTING_AIRPORT("The flight is not leaving from an existing airport!"),
    FLIGHT_DESTINATION_AIRPORT("The flight is not going to an existing airport!");


    private String message;

    ConstraintViolations(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
