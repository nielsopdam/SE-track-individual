package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Airplane;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AirplaneRepository extends CrudRepository<Airplane, Long> {
    @Query("SELECT COUNT(a) > a.location.numberRunways FROM Airplane a WHERE a.location.id = :id")
    boolean isOverbooked(@Param("id") long id);
}