package com.capgemini.setrack.controller;

import com.capgemini.setrack.exception.InvalidModelException;
import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.repository.AirplaneRepository;
import com.capgemini.setrack.utility.ValidationUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(rollbackFor={Exception.class})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Airplane addAirplane(@RequestBody Airplane airplane) throws InvalidModelException {
        airplane.validate();

        try{
            this.airplaneRepository.save(airplane);
            if(this.airplaneRepository.isOverbooked(airplane.getLocation().getId())){
                throw new InvalidModelException("There is no room left at this airport!");
            }
            return airplane;
        } catch(DataIntegrityViolationException e){
            throw ValidationUtility.getInvalidModelException(e);
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Airplane updateAirplane(@RequestBody Airplane airplane) throws InvalidModelException {
        airplane.validate();

        try{
            this.airplaneRepository.save(airplane);
            if(this.airplaneRepository.isOverbooked(airplane.getLocation().getId())){
                throw new InvalidModelException("There is no room left at this airport!");
            }
            return airplane;
        } catch(DataIntegrityViolationException e){
            throw ValidationUtility.getInvalidModelException(e);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteAirplane(@PathVariable long id) {
        this.airplaneRepository.delete(id);
    }

    @RequestMapping(value = "/{id}/gas", method = RequestMethod.POST)
    public void gasAirplane(@PathVariable long id) {
        Airplane airplane = this.airplaneRepository.findOne(id);
        airplane.setFuelLeft(airplane.getFuelCapacity());

        this.airplaneRepository.save(airplane);
    }
}
