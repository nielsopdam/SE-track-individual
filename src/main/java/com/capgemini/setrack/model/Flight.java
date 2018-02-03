package com.capgemini.setrack.model;

import com.capgemini.setrack.converter.LocalDateTimeDeserializer;
import com.capgemini.setrack.converter.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table( name="Flight", uniqueConstraints= {
        @UniqueConstraint(name = "UK_FLIGHT_LIFTOFF", columnNames = {"airplane_id", "origin_id", "liftOffTime"})
})
public class Flight extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message="A flight number is required!")
    @Size(min=2, max=20, message="A flight number must be between 2 and 20 characters long!")
    private String flightNumber;

    @ManyToOne
    @JoinColumn(name="airplane_id", foreignKey=@ForeignKey(name = "FK_FLIGHT_AIRPLANE"))
    @NotNull(message="A flight has to be made by a plane!")
    private Airplane airplane;

    @ManyToOne
    @JoinColumn(name="origin_id", foreignKey=@ForeignKey(name = "FK_FLIGHT_STARTING_AIRPORT"))
    @NotNull(message="A flight has to start somewhere!")
    private Airport origin;

    @ManyToOne
    @JoinColumn(name="destination_id", foreignKey=@ForeignKey(name = "FK_FLIGHT_DESTINATION_AIRPORT"))
    @NotNull(message="A flight has to go somewhere!")
    private Airport destination;

    @NotNull(message="A flight has to have a lift off time!")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime liftOffTime;

    @NotNull(message="A flight has to have a landing time!")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime landingTime;

    @NotNull(message="A duration is required!")
    @Min(value=1L, message="A flight always takes at least 10 minutes!")
    private long duration;

    @NotNull(message="There should be an amount of fuel left!")
    @Min(value=0, message="An amount of fuel has to be at least 0!")
    private int fuelLeft;

    @NotNull(message="There should be an amount of fuel!")
    @Min(value=0, message="An amount of fuel has to be at least 0!")
    private int startingFuel;

    @NotNull(message="A flight has to have a distance!")
    @Min(value=1, message="The distance of a flight has to be at least 1!")
    private int distance;

    @NotNull(message="There should be an amount of distance left!")
    @Min(value=0, message="An amount of distance has to be at least 0!")
    private int distanceLeft;

    @NotNull(message="An airplane has to have a mileage!")
    @Min(value=0, message="The mileage has to be at least 1!")
    private int mileage;

    private int timeFlown;

    public Flight(){}

    public Flight(String flightNumber, Airplane airplane, Airport origin, Airport destination, LocalDateTime liftOffTime) throws Exception {
        this.flightNumber = flightNumber;
        this.airplane = airplane;
        this.origin = origin;
        this.liftOffTime = liftOffTime;
        this.destination = destination;
        this.startingFuel = airplane.getFuelLeft();
    }

    public void setTimeFlown(){
        this.timeFlown = (int)ChronoUnit.SECONDS.between(this.liftOffTime, LocalDateTime.now());
    }

    public int getTimeFlown(){
        return this.timeFlown;
    }

    public int setTimeFlown(int timeFlown){
        return timeFlown;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public Airport getOrigin() {
        return origin;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public long getDuration() {
        return duration;
    }

    public int getStartingFuel() {
        return startingFuel;
    }

    public void setStartingFuel(int startingFuel) {
        this.startingFuel = startingFuel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getLiftOffTime() {
        return liftOffTime;
    }

    public void setLiftOffTime(LocalDateTime liftOffTime) {
        this.liftOffTime = liftOffTime;
    }

    public LocalDateTime getLandingTime() {
        return landingTime;
    }

    public void setLandingTime(LocalDateTime landingTime) {
        this.landingTime = landingTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getFuelLeft() {
        return this.startingFuel - this.timeFlown * this.airplane.getSpeed() / this.airplane.getMileage();
    }

    public void setFuelLeft(int fuelLeft) {
        this.fuelLeft = fuelLeft;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistanceLeft() {
        return distanceLeft;
    }

    public void setDistanceLeft(int distanceLeft) {
        this.distanceLeft = distanceLeft;
    }

    public void setDistanceLeft(){
        this.distanceLeft = this.distance - this.timeFlown * this.airplane.getSpeed();
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
}
