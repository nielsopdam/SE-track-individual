package com.capgemini.setrack.utility;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Component that calculates the distances between cities.
 */

@Component
public class APIDistanceCalculator implements DistanceCalculator {

    /**
     * Retrieves the distance between two cities via api from nl.afstand.org.
     *
     * @param origin the name of the city to travel from
     * @param destination the name of the city to travel to
     *
     * @return distance between the cities in km
     */
    public int getDistanceBetweenCities(String origin, String destination) throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget resource = client.target("http://nl.afstand.org/route.json?stops=" + origin + "%7C" + destination);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String responseString = response.readEntity(String.class);
            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject.getInt("distance");
        } else {
            throw new Exception("Could not get the distance!");
        }
    }
}
