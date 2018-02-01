package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Flight;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FlightRepository extends CrudRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin AND f.liftOffTime BETWEEN :from AND :to")
    Iterable<Flight> findDepartures(@Param("origin") long destination,
                                                  @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin")
    Iterable<Flight> findDepartures(@Param("origin") long destination);

    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin AND f.liftOffTime > :from")
    Iterable<Flight> findDepartures(@Param("origin") long destination, @Param("from") LocalDateTime from);

    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination AND f.liftOffTime BETWEEN :from AND :to")
    Iterable<Flight> findArrivals(@Param("destination") long destination,
                                    @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination")
    Iterable<Flight> findArrivals(@Param("destination") long destination);

    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination AND f.liftOffTime > :from")
    Iterable<Flight> findArrivals(@Param("destination") long destination, @Param("from") LocalDateTime from);
}