package com.capgemini.setrack.utility;

/**
 * Interface class that helps calculating the distance between cities.
 */
public interface DistanceCalculator {

    /**
     * Retrieves the distance between two cities.
     *
     * @param origin the name of the city to travel from
     * @param destination the name of the city to travel to
     *
     * @return distance between the cities in km
     */
    public int getDistanceBetweenCities(String origin, String destination) throws Exception;
}
