package dev.legrug.transaction;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.UUID;

@Path("/client/name/")
@RegisterRestClient(configKey="name-api")
public interface NameService {

    @GET
    @Path("/{clientId}")
    String findName(@PathParam("clientId") UUID clientId);

}
