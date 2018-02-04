package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.model.Flight;
import com.capgemini.setrack.repository.AirplaneRepository;
import com.capgemini.setrack.repository.AirportRepository;
import com.capgemini.setrack.repository.FlightRepository;
import com.capgemini.setrack.utility.DistanceCalculator;
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
public class FlightControllerTests {

    @InjectMocks
    private FlightController flightController;

    @Mock
    private AirplaneRepository airplaneRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private DistanceCalculator distanceCalculator;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(flightController).build();
    }

    @Test
    public void testGetAllFlights() throws Exception {
        Airplane airplane1 = new Airplane("a123", 10, new Airport(), 10, 10);
        Airplane airplane2 = new Airplane("a124", 10, new Airport(), 10, 10);

        Flight flight1 = new Flight("a123", airplane1, new Airport(), new Airport(), LocalDateTime.now().minusMinutes(1));
        Flight flight2 = new Flight("a124", airplane2, new Airport(), new Airport(), LocalDateTime.now());

        List<Flight> flights = new ArrayList<>();
        flights.add(flight1);
        flights.add(flight2);

        Mockito.when(flightRepository.findAll()).thenReturn(flights);

        this.mockMvc.perform(get("/api/flights")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.[0].flightNumber", is(flights.get(0).getFlightNumber())))
                .andExpect(jsonPath("$.[1].flightNumber", is(flights.get(1).getFlightNumber())))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateFlight() throws Exception {
        Airport origin = new Airport("Netherlands", "Eindhoven", 1000, 4);
        Airplane airplane = new Airplane("a123", 500, origin, 10, 10);
        airplane.setFuelLeft(airplane.getFuelCapacity());
        Airport destination = new Airport("Belgium", "Brussel", 2000, 5);
        Flight flight = new Flight("a123", airplane, origin, destination, LocalDateTime.now().minusMinutes(1));
        int distanceToTravel = 430;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(flight);

        Mockito.when(distanceCalculator.getDistanceBetweenCities(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(distanceToTravel);
        Mockito.when(airportRepository.findOne(origin.getId())).thenReturn(origin);
        Mockito.when(airportRepository.findOne(destination.getId())).thenReturn(destination);
        Mockito.when(airplaneRepository.findOne(airplane.getId())).thenReturn(airplane);
        Mockito.when(flightRepository.save(Mockito.any(Flight.class))).thenReturn(flight);

        this.mockMvc.perform(post("/api/flights/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.flightNumber", is(flight.getFlightNumber())))
                .andExpect(jsonPath("$.airplane.airplaneNumber", is(flight.getAirplane().getAirplaneNumber())))
                .andExpect(jsonPath("$.distance", is(distanceToTravel)))
                .andExpect(jsonPath("$.distanceLeft", is(distanceToTravel)))
                .andExpect(jsonPath("$.startingFuel", is((int) airplane.getFuelLeft())))
                .andExpect(status().isOk());

        Mockito.verify(airportRepository, Mockito.times(2)).findOne(Mockito.anyLong());
    }

    @Test
    public void testCreateFlightOnNonExistingAirportReturnError() throws Exception {
        Airplane airplane = new Airplane("a123", 500, new Airport(), 10, 10);
        Airport origin = new Airport("Netherlands", "Eindhoven", 1000, 4);
        Airport destination = new Airport("Belgium", "Brussel", 2000, 1);
        Flight flight = new Flight("a123", airplane, origin, destination, LocalDateTime.now().minusMinutes(1));
        airplane.setFuelLeft(500);
        int distanceToTravel = 430;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(flight);

        Mockito.when(flightRepository.save(Mockito.any(Flight.class)))
                .thenThrow(new DataIntegrityViolationException("foreign key constraint: `FK_FLIGHT_STARTING_AIRPORT`"));
        Mockito.when(distanceCalculator.getDistanceBetweenCities(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(distanceToTravel);
        Mockito.when(airportRepository.findOne(origin.getId())).thenReturn(origin);
        Mockito.when(airportRepository.findOne(destination.getId())).thenReturn(destination);
        Mockito.when(airplaneRepository.findOne(airplane.getId())).thenReturn(airplane);

        this.mockMvc.perform(post("/api/flights/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(flightRepository, Mockito.times(1)).save(Mockito.any(Flight.class));
    }

    @Test
    public void testCreateFlightWithNotEnoughFuelReturnError() throws Exception {
        Airplane airplane = new Airplane("a123", 500, new Airport(), 10, 10);
        Airport origin = new Airport("Netherlands", "Eindhoven", 1000, 4);
        Airport destination = new Airport("Belgium", "Brussel", 2000, 1);
        Flight flight = new Flight("a123", airplane, origin, destination, LocalDateTime.now().minusMinutes(1));
        int distanceToTravel = 430;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(flight);

        Mockito.when(distanceCalculator.getDistanceBetweenCities(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(distanceToTravel);
        Mockito.when(airportRepository.findOne(origin.getId())).thenReturn(origin);
        Mockito.when(airportRepository.findOne(destination.getId())).thenReturn(destination);
        Mockito.when(airplaneRepository.findOne(airplane.getId())).thenReturn(airplane);
        Mockito.when(flightRepository.save(Mockito.any(Flight.class))).thenReturn(flight);

        this.mockMvc.perform(post("/api/flights/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testUpdateFlight() throws Exception {
        Airplane airplane = new Airplane("a123", 500, new Airport(), 10, 10);
        airplane.setFuelLeft(airplane.getFuelCapacity());
        Airport origin = new Airport("Netherlands", "Eindhoven", 1000, 4);
        Airport destination = new Airport("Belgium", "Brussel", 2000, 5);
        Flight flight = new Flight("a123", airplane, origin, destination, LocalDateTime.now().minusMinutes(1));
        int distanceToTravel = 430;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(flight);

        Mockito.when(distanceCalculator.getDistanceBetweenCities(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(distanceToTravel);
        Mockito.when(airportRepository.findOne(origin.getId())).thenReturn(origin);
        Mockito.when(airportRepository.findOne(destination.getId())).thenReturn(destination);
        Mockito.when(airplaneRepository.findOne(airplane.getId())).thenReturn(airplane);
        Mockito.when(flightRepository.save(Mockito.any(Flight.class))).thenReturn(flight);

        this.mockMvc.perform(post("/api/flights/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.flightNumber", is(flight.getFlightNumber())))
                .andExpect(jsonPath("$.airplane.airplaneNumber", is(flight.getAirplane().getAirplaneNumber())))
                .andExpect(jsonPath("$.distance", is(distanceToTravel)))
                .andExpect(jsonPath("$.distanceLeft", is(distanceToTravel)))
                .andExpect(jsonPath("$.startingFuel", is((int) airplane.getFuelLeft())))
                .andExpect(status().isOk());

        Mockito.verify(airportRepository, Mockito.times(2)).findOne(Mockito.anyLong());
    }

    @Test
    public void testUpdateFlightToNonExistingAirportReturnError() throws Exception {
        Airplane airplane = new Airplane("a123", 500, new Airport(), 10, 10);
        Airport origin = new Airport("Netherlands", "Eindhoven", 1000, 4);
        Airport destination = new Airport("Belgium", "Brussel", 2000, 1);
        Flight flight = new Flight("a123", airplane, origin, destination, LocalDateTime.now().minusMinutes(1));
        airplane.setFuelLeft(500);
        int distanceToTravel = 430;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(flight);

        Mockito.when(flightRepository.save(Mockito.any(Flight.class)))
                .thenThrow(new DataIntegrityViolationException("foreign key constraint: `FK_FLIGHT_STARTING_AIRPORT`"));
        Mockito.when(distanceCalculator.getDistanceBetweenCities(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(distanceToTravel);
        Mockito.when(airportRepository.findOne(origin.getId())).thenReturn(origin);
        Mockito.when(airportRepository.findOne(destination.getId())).thenReturn(destination);
        Mockito.when(airplaneRepository.findOne(airplane.getId())).thenReturn(airplane);

        this.mockMvc.perform(post("/api/flights/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(flightRepository, Mockito.times(1)).save(Mockito.any(Flight.class));
    }

    @Test
    public void testUpdateFlightWithNoRunwayFreeReturnError() throws Exception {
        Airplane airplane = new Airplane("a123", 500, new Airport(), 10, 10);
        Airport origin = new Airport("Netherlands", "Eindhoven", 1000, 4);
        Airport destination = new Airport("Belgium", "Brussel", 2000, 0);
        Flight flight = new Flight("a123", airplane, origin, destination, LocalDateTime.now().minusMinutes(1));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(flight);

        Mockito.when(airportRepository.findOne(destination.getId())).thenReturn(destination);
        Mockito.when(airplaneRepository.findOne(airplane.getId())).thenReturn(airplane);
        Mockito.when(flightRepository.save(Mockito.any(Flight.class))).thenReturn(flight);

        this.mockMvc.perform(post("/api/flights/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airportRepository, Mockito.times(1)).findOne(Mockito.anyLong());
        Mockito.verify(distanceCalculator, Mockito.times(0))
                .getDistanceBetweenCities(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(airplaneRepository, Mockito.times(0)).findOne(Mockito.anyLong());
    }

    @Test
    public void testDeleteFlight() throws Exception {
        Mockito.doNothing().when(flightRepository).delete(Mockito.anyLong());

        this.mockMvc.perform(delete("/api/flights/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(flightRepository, Mockito.times(1)).delete((long) 1);
    }
}