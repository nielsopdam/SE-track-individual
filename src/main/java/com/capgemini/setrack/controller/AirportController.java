package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.AirportRepository;
import com.capgemini.setrack.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private FlightRepository flightRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Airport> getAllAirports() {
        return this.airportRepository.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Airport addAirport(@RequestBody Airport airport){
        this.airportRepository.save(airport);
        return airport;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Airport updateAirport(@RequestBody Airport airport) {
        this.airportRepository.save(airport);
        return airport;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteAirport(@PathVariable long id) {
        this.airportRepository.delete(id);
    }

    @RequestMapping(value = "/{id}/departures", method = RequestMethod.GET)
    public Iterable<Flight> getDepartures(
            @PathVariable long id,
            @RequestParam(name="from", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fromDate,
            @RequestParam(name="to", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime toDate) {

        this.flightRepository.updateFlightPlans(id);
        System.out.println("Should have updated..");

        if(fromDate == null && toDate == null){
            return this.flightRepository.findDepartures(id);
        } else if (fromDate == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse("1900-01-01 00:00", formatter);
            return this.flightRepository.findDepartures(id, dateTime, toDate);
        } else {
            return this.flightRepository.findDepartures(id, fromDate);
        }
    }

    @RequestMapping(value = "/{id}/arrivals", method = RequestMethod.GET)
    public Iterable<Flight> getArrivals(
            @PathVariable long id,
            @RequestParam(name="from", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fromDate,
            @RequestParam(name="to", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime toDate) {

        this.flightRepository.updateFlightPlans(id);
        System.out.println("Should have updated..");
        if(fromDate == null && toDate == null){
            return this.flightRepository.findArrivals(id);
        } else if (fromDate == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse("1900-01-01 00:00", formatter);
            return this.flightRepository.findArrivals(id, dateTime, toDate);
        } else {
            return this.flightRepository.findArrivals(id, fromDate);
        }
    }

}