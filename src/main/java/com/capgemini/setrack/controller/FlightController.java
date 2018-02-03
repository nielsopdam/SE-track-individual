package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.AirplaneRepository;
import com.capgemini.setrack.repository.AirportRepository;
import com.capgemini.setrack.repository.FlightRepository;
import com.capgemini.setrack.utility.APIDistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirplaneRepository airplaneRepository;

    @Autowired
    private AirportRepository airportRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Flight> getAllFlights() {
        return this.flightRepository.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Flight addFlight(@RequestBody Flight flight) throws Exception {
        flight.validate();

        flight = this.setFlightValues(flight);

        this.flightRepository.save(flight);
        return flight;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Flight updateFlight(@RequestBody Flight flight) throws Exception {
        flight.validate();

        flight = this.setFlightValues(flight);

        this.flightRepository.save(flight);
        return flight;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteFlight(@PathVariable long id) {
        this.flightRepository.delete(id);
    }

    private Flight setFlightValues(Flight flight) throws Exception {

        Airport destination = airportRepository.findOne(flight.getDestination().getId());

        if(!destination.runwayFree()){
            throw new Exception("There is no runway free at this destination!");
        }

        flight.setAirplane(airplaneRepository.findOne(flight.getAirplane().getId()));
        flight.setOrigin(airportRepository.findOne(flight.getOrigin().getId()));
        flight.setDestination(destination);

        flight.setStartingFuel(flight.getAirplane().getFuelLeft());
        flight.setDistance(new APIDistanceCalculator().getDistanceBetweenCities(flight.getOrigin().getCity(),
                flight.getDestination().getCity()));

        if(flight.getStartingFuel() * flight.getAirplane().getMileage() < flight.getDistance()){
            throw new Exception("There is not enough fuel to make the trip!");
        }

        flight.setDistanceLeft(flight.getDistance());
        flight.setFuelLeft(flight.getStartingFuel());
        flight.setMileage(flight.getAirplane().getMileage());
        flight.setLandingTime(flight.getLiftOffTime().plusSeconds(flight.getDistance() / flight.getAirplane().getSpeed()));
        flight.setDuration(ChronoUnit.SECONDS.between(flight.getLiftOffTime(), flight.getLandingTime()));

        return flight;
    }
}