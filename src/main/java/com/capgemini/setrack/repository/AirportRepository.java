package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AirportRepository extends CrudRepository<Airport, Long> {
    @Query("SELECT a from Airplane a WHERE a.location.id IS NOT NULL AND a.location.id = :id AND " +
            "NOT EXISTS (SELECT (f) FROM Flight f WHERE f.distanceLeft > 0 AND f.airplane.id = a.id)")
    Iterable<Airplane> getAvailableAirplanes(@Param("id") long id);

    @Query("SELECT COUNT(a) from Airplane a WHERE a.location.id = :id")
    int getNumberOfAirplanes(@Param("id") long id);

    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin AND f.liftOffTime BETWEEN :from AND :to ORDER BY f.liftOffTime")
    Iterable<Flight> findDepartures(@Param("origin") long destination,
                                    @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination AND f.liftOffTime BETWEEN :from AND :to ORDER BY f.landingTime")
    Iterable<Flight> findArrivals(@Param("destination") long destination,
                                  @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}