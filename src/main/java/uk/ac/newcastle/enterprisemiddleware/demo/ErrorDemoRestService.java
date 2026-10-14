package uk.ac.newcastle.enterprisemiddleware.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/demo/errors")
@Produces(MediaType.APPLICATION_JSON)
public class ErrorDemoRestService {

    @GET
    @Path("/unexpected")
    public void triggerUnexpectedError() {
        throw new IllegalStateException("Demonstration-only unexpected error");
    }
}
