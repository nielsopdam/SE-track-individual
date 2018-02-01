package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Flight> getAllFlights() {
        return this.flightRepository.findAll();
    }
}