package com.capgemini.setrack.controller;

import com.capgemini.setrack.exception.InvalidModelException;
import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.AirplaneRepository;
import com.capgemini.setrack.repository.AirportRepository;
import com.capgemini.setrack.repository.FlightRepository;
import com.capgemini.setrack.utility.DistanceCalculator;
import com.capgemini.setrack.utility.ValidationUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Autowired
    private DistanceCalculator distanceCalculator;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Flight> getAllFlights() {
        return this.flightRepository.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Flight addFlight(@RequestBody Flight flight) throws Exception {
        flight = this.setFlightValues(flight);
        flight.validate();

        try{
            this.flightRepository.save(flight);
            return flight;
        } catch(DataIntegrityViolationException e){
            throw ValidationUtility.getInvalidModelException(e);
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Flight updateFlight(@RequestBody Flight flight) throws Exception {
        flight = this.setFlightValues(flight);
        flight.validate();

        try{
            this.flightRepository.save(flight);
            return flight;
        } catch(DataIntegrityViolationException e){
            throw ValidationUtility.getInvalidModelException(e);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteFlight(@PathVariable long id) {
        this.flightRepository.delete(id);
    }

    private Flight setFlightValues(Flight flight) throws Exception {
        Airport destination = airportRepository.findOne(flight.getDestination().getId());

        if(!destination.runwayFree()){
            throw new InvalidModelException("There is no runway free at this destination!");
        }

        flight.setAirplane(airplaneRepository.findOne(flight.getAirplane().getId()));

        flight.setOrigin(airportRepository.findOne(flight.getOrigin().getId()));
        flight.setDestination(destination);

        flight.setStartingFuel(flight.getAirplane().getFuelLeft());
        flight.setDistance(this.distanceCalculator.getDistanceBetweenCities(flight.getOrigin().getCity(),
                flight.getDestination().getCity()));

        if(!flight.hasEnoughFuel()){
            throw new InvalidModelException("There is not enough fuel to make the trip!");
        }

        flight.setDistanceLeft(flight.getDistance());
        flight.setFuelLeft(flight.getStartingFuel());
        flight.setMileage(flight.getAirplane().getMileage());
        flight.setLandingTime(flight.getLiftOffTime().plusSeconds(flight.getDistance() / flight.getAirplane().getSpeed()));
        flight.setDuration(ChronoUnit.SECONDS.between(flight.getLiftOffTime(), flight.getLandingTime()));

        return flight;
    }
}