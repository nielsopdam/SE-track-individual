package com.capgemini.setrack.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table( name="Flight", uniqueConstraints= {
        @UniqueConstraint(name = "UK_AIRPORT_COUNTRY_CITY", columnNames = {"country", "city"})
})
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message="A flight has to be made by a plane!")
    private Airplane airplane;

    @NotNull(message="A flight has to start somewhere!")
    private Airport origin;

    @NotNull(message="A flight has to go somewhere!")
    private Airport destination;

    @NotNull(message="A flight has to have a lift off time!")
    private LocalDateTime liftOffTime;

    @NotNull(message="A flight has to have a landing time!")
    private LocalDateTime landingTime;

    @NotNull(message="A duration is required!")
    @Min(value=1, message="A flight always takes at least 10 minutes!")
    private int duration;

    @NotNull(message="An amount of fuel is required!")
    @Min(value=1, message="A flight always costs at least 1 kg of fuel!")
    private int fuel;

    public Flight(){}

    public Flight(Airplane airplane, Airport origin, Airport destination, LocalDateTime liftOffTime, LocalDateTime landingTime, int duration, int fuel) {
        this.airplane = airplane;
        this.origin = origin;
        this.liftOffTime = liftOffTime;
        this.landingTime = landingTime;
        this.destination = destination;
        this.duration = duration;
        this.fuel = fuel;
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

    public int getDuration() {
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
}
