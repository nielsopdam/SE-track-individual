package com.capgemini.setrack.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table( name="Airport", uniqueConstraints= {
        @UniqueConstraint(name = "UK_AIRPORT_COUNTRY_CITY", columnNames = {"country", "city"})
})
public class Airport extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message="A country is required!")
    @Size(min=2, max=50, message="The name of a country should be between 2 and 50 characters long!")
    private String country;

    @NotNull(message="A city is required!")
    @Size(min=2, max=70, message="The name of a city should be between 2 and 70 characters long!")
    private String city;

    @NotNull(message="A budget is required!")
    @Min(value=1, message="The budget has to be at least 1 euro!")
    private int budget;

    @NotNull(message="An airport should have at least one runway!")
    @Min(value=1, message="An airport should have at least runway!")
    private int numberRunways;

    @OneToMany(mappedBy="location")
    private List<Airplane> airplanes;

    public Airport(){}

    public Airport(String country, String city, int budget, int numberRunways) {
        this.country = country;
        this.city = city;
        this.budget = budget;
        this.numberRunways = numberRunways;
    }

    public boolean runwayFree(){
        return this.airplanes.size() < this.numberRunways;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getNumberRunways() {
        return numberRunways;
    }

    public void setNumberRunways(int numberRunways) {
        this.numberRunways = numberRunways;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
