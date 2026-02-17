package ban.resources;

import ban.model.Data;
import ban.services.StationCache;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/stations")
public class StationsResource {

    private StationCache cache = new StationCache();

    /**
     * GET /ban/stations/list
     * Devuelve la lista de estaciones Bicing (desde caché)
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStations() {
        try {
            Data data = cache.getStations();
            if (data == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\": \"Unable to fetch data from Bicing\"}")
                        .build();
            }
            // Return the list of stations directly as per requirement "Returns the stations
            // list"
            // The User's example returns "Data" object (wrapper), but let's check
            // Requirement: "Returns the stations list"
            // Actually the User's code snippet for StationsResource returns: `return
            // Response.ok(data).build();`
            // where `data` is the wrapper.
            // But strict requirement says "Returns the stations list".
            // However, to follow the USER'S CODE example strictly (as they asked):
            return Response.ok(data).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
