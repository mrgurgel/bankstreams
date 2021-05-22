package dev.legrug;

import com.github.javafaker.Faker;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/client/name")
public class NamingResource {

    Integer counter = 0;

    @GET
    @Path("{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String findName(@PathParam("clientId") String clientId) {
        counter++;
        if(isItTimeOfAlcaponeName()) {
            return "Al Capone " + counter;
        }
        return new Faker().name().fullName();
    }

    private boolean isItTimeOfAlcaponeName() {
        return counter % 10 == 0;
    }
}