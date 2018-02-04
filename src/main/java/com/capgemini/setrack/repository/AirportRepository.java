package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AirportRepository extends CrudRepository<Airport, Long> {
    /**
     * Returns all the airplanes that are currently at the airport and not yet have a flight scheduled.
     *
     * @param id The airport for which the available airplanes are retrieved
     * @return A list of the airplanes that are currently available
     */
    @Query("SELECT a from Airplane a WHERE a.location.id IS NOT NULL AND a.location.id = :id AND " +
            "NOT EXISTS (SELECT (f) FROM Flight f WHERE f.distanceLeft > 0 AND f.airplane.id = a.id)")
    Iterable<Airplane> getAvailableAirplanes(@Param("id") long id);

    /**
     * Returns the number of airplanes at an airport.
     *
     * @param id The airport for which to check the number of airplanes.
     * @return the number of airplanes at the airport
     */
    @Query("SELECT COUNT(a) from Airplane a WHERE a.location.id = :id")
    int getNumberOfAirplanes(@Param("id") long id);

    /**
     * Returns all the flights that are scheduled to depart at a specific airport.
     *
     * @param origin The airport from which the flights are to be departing.
     * @param from Only flights leaving after this time are retrieved.
     * @param to Only flights leaving before this time are retrieved.
     *
     * @return A list of the airplanes that are to depart
     */
    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin AND f.liftOffTime BETWEEN :from AND :to ORDER BY f.liftOffTime")
    Iterable<Flight> findDepartures(@Param("origin") long origin,
                                    @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Returns all the flights that are scheduled to arrive at a specific airport.
     *
     * @param destination The airport from which the flights are to be arriving.
     * @param from Only flights arriving after this time are retrieved.
     * @param to Only flights arriving before this time are retrieved.
     *
     * @return A list of the airplanes that are to arrive
     */
    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination AND f.liftOffTime BETWEEN :from AND :to ORDER BY f.landingTime")
    Iterable<Flight> findArrivals(@Param("destination") long destination,
                                  @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}