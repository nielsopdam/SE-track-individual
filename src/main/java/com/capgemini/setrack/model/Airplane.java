package com.capgemini.setrack.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table( name="Airplane", uniqueConstraints= {
        @UniqueConstraint(name = "UK_AIRPLANE_FLIGHTNUMBER", columnNames = {"airplaneNumber"})
})
public class Airplane extends Model {
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

    @NotNull(message="An airplane has to have a speed!")
    @Min(value=1, message="The speed has to be at least 1!")
    private int speed;

    @NotNull(message="An airplane has to have a mileage!")
    @Min(value=0, message="The mileage has to be at least 1!")
    private int mileage;

    public Airplane(){}

    public Airplane(String airplaneNumber, int fuelCapacity, Airport location, int speed, int mileage) {
        this.airplaneNumber = airplaneNumber;
        this.fuelCapacity = fuelCapacity;
        this.location = location;
        this.speed = speed;
        this.mileage = mileage;
    }

    public Airplane(String airplaneNumber, int fuelCapacity) {
        this.airplaneNumber = airplaneNumber;
        this.fuelCapacity = fuelCapacity;
        this.location = null;
        this.speed = 100;
        this.mileage = 10;
    }

    public Airplane(String airplaneNumber){
        this.airplaneNumber = airplaneNumber;
        this.fuelCapacity = 5000;
        this.location = null;
        this.speed = 100;
        this.mileage = 10;
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

    public void setLocation(Flight flight, long prevDistanceLeft){
        int curDistanceLeft = flight.getDistanceLeft();

        if(curDistanceLeft > 0 && prevDistanceLeft > 0 && prevDistanceLeft > curDistanceLeft){
            System.out.println("Plane in the air");
            this.location = null;
        } else if (prevDistanceLeft > 0 && curDistanceLeft == 0){
            this.location = flight.getDestination();
            System.out.println("Plane at destination");
        } else if (curDistanceLeft > prevDistanceLeft) {
            this.location = flight.getOrigin();
            System.out.println("Plane at origin");
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
}
