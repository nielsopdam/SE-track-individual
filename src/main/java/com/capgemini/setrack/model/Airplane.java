package com.capgemini.setrack.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table( name="Airplane", uniqueConstraints= {
        @UniqueConstraint(name = "UK_AIRPLANE_FLIGHTNUMBER", columnNames = {"flightNumber"})
})
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message="A flight number is required!")
    @Size(min=2, max=20, message="A flight number must be between 2 and 20 characters long!")
    private String flightNumber;

    @NotNull(message="A fuel capacity is required!")
    @Min(value=10, message="The fuel capacity should be at least 10!")
    private int fuelCapacity;

    @NotNull(message="A plane can't have a negative amount of fuel!")
    @Min(value=0, message="The fuel capacity can't be negative!")
    private int fuelLeft;

    public Airplane(){}

    public Airplane(String flightNumber, int fuelCapacity) {
        this.flightNumber = flightNumber;
        this.fuelCapacity = fuelCapacity;
    }

    public Airplane(String flightNumber){
        this.flightNumber = flightNumber;
        this.fuelCapacity = 5000;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public int getFuelLeft() {
        return fuelLeft;
    }

    public void setFuelLeft(int fuelLeft) {
        this.fuelLeft = fuelLeft;
    }
}
