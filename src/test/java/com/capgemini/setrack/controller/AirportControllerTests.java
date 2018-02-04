package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.AirplaneRepository;
import com.capgemini.setrack.repository.AirportRepository;
import com.capgemini.setrack.repository.FlightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
@SpringBootTest
public class AirportControllerTests {

    @InjectMocks
    private AirportController airportController;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirplaneRepository airplaneRepository;

    @Mock
    private FlightRepository flightRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(airportController).build();
    }

    @Test
    public void testGetAllAirports() throws Exception {
        Airport airport1 = new Airport("Netherlands", "Eindhoven", 1000, 4);
        Airport airport2 = new Airport("Belgium", "Brussel", 2000, 5);

        List<Airport> airports = new ArrayList<>();
        airports.add(airport1);
        airports.add(airport2);

        Mockito.when(airportRepository.findAll()).thenReturn(airports);

        this.mockMvc.perform(get("/api/airports")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.[0].city", is(airports.get(0).getCity())))
                .andExpect(jsonPath("$.[1].city", is(airports.get(1).getCity())))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateAirport() throws Exception {
        Airport airport = new Airport("Netherlands", "Eindhoven", 1000, 4);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airport);

        Mockito.when(airportRepository.save(Mockito.any(Airport.class))).thenReturn(airport);

        this.mockMvc.perform(post("/api/airports/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.country", is(airport.getCountry())))
                .andExpect(jsonPath("$.city", is(airport.getCity())))
                .andExpect(jsonPath("$.budget", is((int)airport.getBudget())))
                .andExpect(jsonPath("$.numberRunways", is((int)airport.getNumberRunways())))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateExistingAirportReturnError() throws Exception {
        Airport airport = new Airport("Netherlands", "Eindhoven", 1000, 4);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airport);

        Mockito.when(airportRepository.save(Mockito.any(Airport.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry: 'UK_AIRPORT_COUNTRY_CITY'"));

        this.mockMvc.perform(post("/api/airports/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airportRepository, Mockito.times(1)).save(Mockito.any(Airport.class));
    }

    @Test
    public void testCreateAirportWithNegativeRunwaysReturnsError() throws Exception {
        Airport airport = new Airport("Netherlands", "Eindhoven", 1000, -1);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airport);

        Mockito.when(airportRepository.save(Mockito.any(Airport.class))).thenReturn(airport);

        this.mockMvc.perform(post("/api/airports/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airportRepository, Mockito.times(0)).save(Mockito.any(Airport.class));
    }

    @Test
    public void testUpdateAirport() throws Exception {
        Airport airport = new Airport("Netherlands", "Eindhoven", 1000, 4);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airport);

        Mockito.when(airportRepository.save(Mockito.any(Airport.class))).thenReturn(airport);

        this.mockMvc.perform(post("/api/airports/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.country", is(airport.getCountry())))
                .andExpect(jsonPath("$.city", is(airport.getCity())))
                .andExpect(jsonPath("$.budget", is((int)airport.getBudget())))
                .andExpect(jsonPath("$.numberRunways", is((int)airport.getNumberRunways())))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateToExistingAirportReturnError() throws Exception {
        Airport airport = new Airport("Netherlands", "Eindhoven", 1000, 4);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airport);

        Mockito.when(airportRepository.save(Mockito.any(Airport.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry: 'UK_AIRPORT_COUNTRY_CITY'"));

        this.mockMvc.perform(post("/api/airports/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airportRepository, Mockito.times(1)).save(Mockito.any(Airport.class));
    }

    @Test
    public void testUpdateAirportWithNegativeRunwaysReturnsError() throws Exception {
        Airport airport = new Airport("Netherlands", "Eindhoven", 1000, -1);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airport);

        Mockito.when(airportRepository.save(Mockito.any(Airport.class))).thenReturn(airport);

        this.mockMvc.perform(post("/api/airports/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airportRepository, Mockito.times(0)).save(Mockito.any(Airport.class));
    }

    @Test
    public void testDeleteAirport() throws Exception {
        Mockito.doNothing().when(airportRepository).delete(Mockito.anyLong());

        this.mockMvc.perform(delete("/api/airports/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(airportRepository, Mockito.times(1)).delete((long) 1);
    }

    @Test
    public void testGetAllAllAvailableAirplanesInAirport() throws Exception {
        Airplane airplane1 = new Airplane("a123", 10, new Airport(), 10, 10);
        Airplane airplane2 = new Airplane("a123", 10, new Airport(), 10, 10);

        List<Airplane> airplanes = new ArrayList<>();
        airplanes.add(airplane1);
        airplanes.add(airplane2);

        Mockito.when(airportRepository.getAvailableAirplanes(1)).thenReturn(airplanes);

        this.mockMvc.perform(get("/api/airports/1/available_planes")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.[0].airplaneNumber", is(airplanes.get(0).getAirplaneNumber())))
                .andExpect(jsonPath("$.[1].airplaneNumber", is(airplanes.get(1).getAirplaneNumber())))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllAllArrivingPlanesInAirport() throws Exception {
        Airplane airplane1 = new Airplane("a123", 10, new Airport(), 10, 10);
        Airplane airplane2 = new Airplane("a123", 10, new Airport(), 10, 10);

        Flight flight1 = new Flight("a123", airplane1, new Airport(), new Airport(), LocalDateTime.now());
        Flight flight2 = new Flight("a124", airplane2, new Airport(), new Airport(), LocalDateTime.now());

        List<Flight> flights = new ArrayList<>();
        flights.add(flight1);
        flights.add(flight2);

        Mockito.when(airportRepository.findArrivals(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class))).thenReturn(flights);
        Mockito.when(airplaneRepository.save(Mockito.any(Airplane.class))).thenReturn(null);

        this.mockMvc.perform(get("/api/airports/1/arrivals")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.[0].flightNumber", is(flights.get(0).getFlightNumber())))
                .andExpect(jsonPath("$.[1].flightNumber", is(flights.get(1).getFlightNumber())))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllAllDepartingPlanesInAirport() throws Exception {
        Airplane airplane1 = new Airplane("a123", 10, new Airport(), 10, 10);
        Airplane airplane2 = new Airplane("a124", 10, new Airport(), 10, 10);

        Flight flight1 = new Flight("a123", airplane1, new Airport(), new Airport(), LocalDateTime.now().minusMinutes(1));
        Flight flight2 = new Flight("a124", airplane2, new Airport(), new Airport(), LocalDateTime.now());

        List<Flight> flights = new ArrayList<>();
        flights.add(flight1);
        flights.add(flight2);

        Mockito.when(airportRepository.findDepartures(Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class))).thenReturn(flights);
        Mockito.when(airplaneRepository.save(Mockito.any(Airplane.class))).thenReturn(null);

        this.mockMvc.perform(get("/api/airports/1/departures")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.[0].flightNumber", is(flights.get(0).getFlightNumber())))
                .andExpect(jsonPath("$.[1].flightNumber", is(flights.get(1).getFlightNumber())))
                .andExpect(status().isOk());
    }
}
