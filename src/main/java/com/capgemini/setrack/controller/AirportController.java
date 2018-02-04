package com.capgemini.setrack.controller;

import com.capgemini.setrack.exception.InvalidModelException;
import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.AirplaneRepository;
import com.capgemini.setrack.repository.AirportRepository;
import com.capgemini.setrack.utility.ValidationUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirplaneRepository airplaneRepository;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final LocalDateTime EARLIESTDATE = LocalDateTime.parse("1900-01-01 00:00", FORMATTER);
    private final LocalDateTime LATESTDATE = LocalDateTime.parse("9999-12-31 00:00", FORMATTER);

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Airport> getAllAirports() {
        return this.airportRepository.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Airport addAirport(@RequestBody Airport airport) throws InvalidModelException {
        airport.validate();

        try{
            this.airportRepository.save(airport);
            return airport;
        } catch(DataIntegrityViolationException e){
            throw ValidationUtility.getInvalidModelException(e);
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Airport updateAirport(@RequestBody Airport airport) throws InvalidModelException {
        airport.validate();

        try{
            this.airportRepository.save(airport);
            return airport;
        } catch(DataIntegrityViolationException e){
            throw ValidationUtility.getInvalidModelException(e);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteAirport(@PathVariable long id) {
        this.airportRepository.delete(id);
    }

    @RequestMapping(value = "/{id}/available_planes", method = RequestMethod.GET)
    public Iterable<Airplane> getAvailableAirplanes(@PathVariable long id){
        return this.airportRepository.getAvailableAirplanes(id);
    }

    @RequestMapping(value = "/{id}/departures", method = RequestMethod.GET)
    public Iterable<Flight> getDepartures(
            @PathVariable long id,
            @RequestParam(name="from", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fromDate,
            @RequestParam(name="to", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime toDate) {

        fromDate = fromDate == null ? this.EARLIESTDATE : fromDate;
        toDate = toDate == null ? this.LATESTDATE : toDate;

        return this.airportRepository.findDepartures(id, fromDate, toDate);
    }

    @RequestMapping(value = "/{id}/arrivals", method = RequestMethod.GET)
    public Iterable<Flight> getArrivals(
            @PathVariable long id,
            @RequestParam(name="from", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fromDate,
            @RequestParam(name="to", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime toDate) {

        fromDate = fromDate == null ? this.EARLIESTDATE : fromDate;
        toDate = toDate == null ? this.LATESTDATE : toDate;

        return this.airportRepository.findArrivals(id, fromDate, toDate);
    }

    /**
     * Ensures flight plans are up to date.
     *
     * @param id the id of the aiport to update the flightplans from
     */
    @RequestMapping(value = "/{id}/update_flightplans", method = RequestMethod.POST)
    public void updateFlightPlans(
            @PathVariable long id) {

        this.updateFlightPlans(this.airportRepository.findArrivals(id, this.EARLIESTDATE, this.LATESTDATE));
        this.updateFlightPlans(this.airportRepository.findDepartures(id, this.EARLIESTDATE, this.LATESTDATE));
    }

    /**
     * Ensures flight plans are up to date by updating distance left and fuel left for flights.
     * Also makes sure an airplane actually switches locations when flying.
     *
     * @param flights the flights to update the flight plan for
     */
    private void updateFlightPlans(Iterable<Flight> flights){
        for(Flight flight: flights){
            Airplane airplane = flight.getAirplane();

            flight.setTimeFlown();
            if(flight.getTimeFlown() <= 0) continue;

            long prevDistanceLeft = flight.getDistanceLeft();
            flight.setDistanceLeft();
            airplane.setFuelLeft(flight.getFuelLeft());
            airplane.setLocation(flight, prevDistanceLeft);

            this.airplaneRepository.save(airplane);
        }
    }
}