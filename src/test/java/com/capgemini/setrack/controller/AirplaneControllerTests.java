package com.capgemini.setrack.controller;

import com.capgemini.setrack.model.Airplane;
import com.capgemini.setrack.model.Airport;
import com.capgemini.setrack.repository.AirplaneRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AirplaneControllerTests {

    @InjectMocks
    private AirplaneController airplaneController;

    @Mock
    private AirplaneRepository airplaneRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(airplaneController).build();
    }

    @Test
    public void testGetAllAirplanes() throws Exception {
        Airplane airplane1 = new Airplane("a123", 10, new Airport(), 10, 10);
        Airplane airplane2 = new Airplane("a123", 10, new Airport(), 10, 10);

        List<Airplane> airplanes = new ArrayList<>();
        airplanes.add(airplane1);
        airplanes.add(airplane2);

        Mockito.when(airplaneRepository.findAll()).thenReturn(airplanes);

        this.mockMvc.perform(get("/api/airplanes")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.[0].airplaneNumber", is(airplanes.get(0).getAirplaneNumber())))
                .andExpect(jsonPath("$.[1].airplaneNumber", is(airplanes.get(1).getAirplaneNumber())))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateAirplane() throws Exception {
        Airplane airplane = new Airplane("a123", 10, new Airport(), 10, 10);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airplane);

        Mockito.when(airplaneRepository.save(Mockito.any(Airplane.class))).thenReturn(airplane);

        this.mockMvc.perform(post("/api/airplanes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.airplaneNumber", is(airplane.getAirplaneNumber())))
                .andExpect(jsonPath("$.fuelCapacity", is((int)airplane.getFuelCapacity())))
                .andExpect(jsonPath("$.fuelLeft", is((int)airplane.getFuelLeft())))
                .andExpect(jsonPath("$.speed", is((int)airplane.getSpeed())))
                .andExpect(jsonPath("$.mileage", is((int)airplane.getMileage())))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateExistingAirplaneReturnError() throws Exception {
        Airplane airplane = new Airplane("a123", 10, new Airport(), 10, 10);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airplane);

        Mockito.when(airplaneRepository.save(Mockito.any(Airplane.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry: 'UK_AIRPLANE_AIRPLANENUMBER'"));

        this.mockMvc.perform(post("/api/airplanes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airplaneRepository, Mockito.times(1)).save(Mockito.any(Airplane.class));
    }

    @Test
    public void testCreateAirplaneWithNegativeSpeedReturnsError() throws Exception {
        Airplane airplane = new Airplane("a123", 10, new Airport(), -1, 10);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airplane);


        this.mockMvc.perform(post("/api/airplanes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airplaneRepository, Mockito.times(0)).save(Mockito.any(Airplane.class));
    }

    @Test
    public void testUpdateAirplane() throws Exception {
        Airplane airplane = new Airplane("a123", 10, new Airport(), 10, 10);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airplane);

        Mockito.when(airplaneRepository.save(Mockito.any(Airplane.class))).thenReturn(airplane);

        this.mockMvc.perform(post("/api/airplanes/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.airplaneNumber", is(airplane.getAirplaneNumber())))
                .andExpect(jsonPath("$.fuelCapacity", is((int)airplane.getFuelCapacity())))
                .andExpect(jsonPath("$.fuelLeft", is((int)airplane.getFuelLeft())))
                .andExpect(jsonPath("$.speed", is((int)airplane.getSpeed())))
                .andExpect(jsonPath("$.mileage", is((int)airplane.getMileage())))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateToExistingAirplaneReturnError() throws Exception {
        Airplane airplane = new Airplane("a123", 10, new Airport(), 10, 10);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airplane);

        Mockito.when(airplaneRepository.save(Mockito.any(Airplane.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry: 'UK_AIRPLANE_AIRPLANENUMBER'"));

        this.mockMvc.perform(post("/api/airplanes/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airplaneRepository, Mockito.times(1)).save(Mockito.any(Airplane.class));
    }

    @Test
    public void testUpdateAirplaneWithNegativeSpeedReturnsError() throws Exception {
        Airplane airplane = new Airplane("a123", 10, new Airport(), -1, 10);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(airplane);


        this.mockMvc.perform(post("/api/airplanes/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        Mockito.verify(airplaneRepository, Mockito.times(0)).save(Mockito.any(Airplane.class));
    }

    @Test
    public void testDeleteAirplane() throws Exception {
        Mockito.doNothing().when(airplaneRepository).delete(Mockito.anyLong());

        this.mockMvc.perform(delete("/api/airplanes/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(airplaneRepository, Mockito.times(1)).delete((long) 1);
    }

    @Test
    public void testGasAirplane() throws Exception {
        Airplane airplane = new Airplane("a123", 10, new Airport(), 10, 10);
        airplane.setFuelLeft(5);

        Assert.assertEquals(airplane.getFuelLeft(), 5);
        Mockito.when(airplaneRepository.findOne(Mockito.anyLong())).thenReturn(airplane);

        this.mockMvc.perform(post("/api/airplanes/1/gas")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Assert.assertEquals(airplane.getFuelLeft(), 10);
        Mockito.verify(airplaneRepository, Mockito.times(1)).findOne((long) 1);
        Mockito.verify(airplaneRepository, Mockito.times(1)).save(Mockito.any(Airplane.class));
    }
}
