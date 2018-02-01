package com.capgemini.setrack.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table( name="Airplane", uniqueConstraints= {
        @UniqueConstraint(name = "UK_AIRPLANE_FLIGHTNUMBER", columnNames = {"airplaneNumber"})
})
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message="An airplane number is required!")
    @Size(min=2, max=20, message="An airplane number number must be between 2 and 20 characters long!")
    private String airplaneNumber;

    @NotNull(message="A fuel capacity is required!")
    @Min(value=10, message="The fuel capacity should be at least 10!")
    private int fuelCapacity;

    @NotNull(message="A plane can't have a negative amount of fuel!")
    @Min(value=0, message="The fuel capacity can't be negative!")
    private int fuelLeft;

    @ManyToOne
    @JoinColumn(name="location_id", foreignKey=@ForeignKey(name = "FK_AIRPLANE_AIRPORT"))
    private Airport location;

    public Airplane(){}

    public Airplane(String airplaneNumber, int fuelCapacity, Airport location) {
        this.airplaneNumber = airplaneNumber;
        this.fuelCapacity = fuelCapacity;
        this.location = location;
    }

    public Airplane(String airplaneNumber, int fuelCapacity) {
        this.airplaneNumber = airplaneNumber;
        this.fuelCapacity = fuelCapacity;
        this.location = null;
    }

    public Airplane(String airplaneNumber){
        this.airplaneNumber = airplaneNumber;
        this.fuelCapacity = 5000;
        this.location = null;
    }

    public String getAirplaneNumber() {
        return airplaneNumber;
    }

    public void setAirplaneNumber(String airplaneNumber) {
        this.airplaneNumber = airplaneNumber;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Airport getLocation() {
        return location;
    }

    public void setLocation(Airport location) {
        this.location = location;
    }
}
