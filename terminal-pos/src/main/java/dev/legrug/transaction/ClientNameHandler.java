package dev.legrug.transaction;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class ClientNameHandler {

    @Inject
    @RestClient
    NameService nameService;

    @Fallback(fallbackMethod = "defaultName")
    public String searchClientName(UUID clientId) {
        return nameService.findName(clientId);
    }

    public String defaultName(UUID clientId) {
        return "Unknown name for: " + clientId.toString();
    }

}
