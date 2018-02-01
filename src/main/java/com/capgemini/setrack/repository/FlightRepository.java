package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Flight;
import org.springframework.data.repository.CrudRepository;

public interface FlightRepository extends CrudRepository<Flight, Long> {
}