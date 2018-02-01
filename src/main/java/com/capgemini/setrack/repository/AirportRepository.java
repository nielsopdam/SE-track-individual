package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Airport;
import org.springframework.data.repository.CrudRepository;

public interface AirportRepository extends CrudRepository<Airport, Long> {
}