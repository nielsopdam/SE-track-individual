package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.model.Airport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AirportRepository extends CrudRepository<Airport, Long> {
    @Query("SELECT a from Airplane a WHERE a.location.id IS NOT NULL AND a.location.id = :id")
    Iterable<Airplane> getAvailableAirplanes(@Param("id") long id);

    @Query("SELECT COUNT(a) from Airplane a WHERE a.location.id = :id")
    int getNumberOfAirplanes(@Param("id") long id);
}