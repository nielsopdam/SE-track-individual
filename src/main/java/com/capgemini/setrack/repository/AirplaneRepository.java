package com.capgemini.setrack.repository;

import com.capgemini.setrack.model.Airplane;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AirplaneRepository extends CrudRepository<Airplane, Long> {
    /**
     * Returns if there are more planes at the airport that possible.
     *
     * @param id The airport to check for.
     *
     * @return True of the airport is overbooked, False otherwise
     */
    @Query("SELECT COUNT(a) > a.location.numberRunways FROM Airplane a WHERE a.location.id = :id")
    boolean isOverbooked(@Param("id") long id);
}