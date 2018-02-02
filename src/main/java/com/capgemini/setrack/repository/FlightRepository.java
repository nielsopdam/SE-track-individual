package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Flight;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public interface FlightRepository extends CrudRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin AND f.liftOffTime BETWEEN :from AND :to ORDER BY f.liftOffTime")
    Iterable<Flight> findDepartures(@Param("origin") long destination,
                                                  @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin ORDER BY f.liftOffTime")
    Iterable<Flight> findDepartures(@Param("origin") long destination);

    @Query("SELECT f FROM Flight f WHERE f.origin.id = :origin AND f.liftOffTime > :from ORDER BY f.liftOffTime")
    Iterable<Flight> findDepartures(@Param("origin") long destination, @Param("from") LocalDateTime from);

    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination AND f.liftOffTime BETWEEN :from AND :to ORDER BY f.landingTime")
    Iterable<Flight> findArrivals(@Param("destination") long destination,
                                    @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination ORDER BY f.landingTime")
    Iterable<Flight> findArrivals(@Param("destination") long destination);

    @Query("SELECT f FROM Flight f WHERE f.destination.id = :destination AND f.liftOffTime > :from ORDER BY f.landingTime")
    Iterable<Flight> findArrivals(@Param("destination") long destination, @Param("from") LocalDateTime from);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Flight as f SET f.distanceLeft = GREATEST(0, (f.distance - (TIMESTAMPDIFF(SECOND, f.liftOffTime, NOW()) + 3600) / f.mileage)) " +
            "WHERE f.origin.id = :origin AND (TIMESTAMPDIFF(SECOND, f.liftOffTime, NOW()) + 3600) > 0")
    void updateFlightPlans(@Param("origin") long origin);
}