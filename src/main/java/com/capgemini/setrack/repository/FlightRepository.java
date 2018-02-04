package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Flight;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface FlightRepository extends CrudRepository<Flight, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Flight as f SET f.distanceLeft = GREATEST(0, (f.distance - (TIMESTAMPDIFF(SECOND, f.liftOffTime, NOW()) + 3600) / f.mileage)) " +
            "WHERE f.origin.id = :origin AND (TIMESTAMPDIFF(SECOND, f.liftOffTime, NOW()) + 3600) > 0")
    void updateFlightPlans(@Param("origin") long origin);
}