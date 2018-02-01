package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.repository.AirplaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/airplanes")
public class AirplaneController {

    @Autowired
    private AirplaneRepository airplaneRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Airplane> getAllPlanes() {
        return this.airplaneRepository.findAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Airplane addAirplane(@RequestBody Airplane airplane){
        this.airplaneRepository.save(airplane);
        return airplane;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Airplane updateAirplane(@RequestBody Airplane airplane) {
        this.airplaneRepository.save(airplane);
        return airplane;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteAirplane(@PathVariable long id) {
        this.airplaneRepository.delete(id);
    }
}
