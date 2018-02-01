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
public class Flight {
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

    private int fuel;

    public Flight(){}

    public Flight(String flightNumber, Airplane airplane, Airport origin, Airport destination, LocalDateTime liftOffTime, LocalDateTime landingTime) {
        this.flightNumber = flightNumber;
        this.airplane = airplane;
        this.origin = origin;
        this.liftOffTime = liftOffTime;
        this.landingTime = landingTime;
        this.destination = destination;
        this.duration = ChronoUnit.MINUTES.between(liftOffTime, landingTime);
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

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
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
}
