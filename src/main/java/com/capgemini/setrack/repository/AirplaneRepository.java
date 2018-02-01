package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Airplane;
import org.springframework.data.repository.CrudRepository;

public interface AirplaneRepository extends CrudRepository<Airplane, Long> {
}