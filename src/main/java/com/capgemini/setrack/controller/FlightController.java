package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Flight> getAllFlights() {
        return this.flightRepository.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Flight addFlight(@RequestBody Flight flight){
        this.flightRepository.save(flight);
        return flight;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Flight updateFlight(@RequestBody Flight flight) {
        this.flightRepository.save(flight);
        return flight;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteFlight(@PathVariable long id) {
        this.flightRepository.delete(id);
    }
}