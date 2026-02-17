package ban.resources;

import ban.clients.BicingClient;
import ban.model.Data;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/stations")
public class StationsResource {

    // Cache logic integrated directly into the Resource
    private static Data cachedData = null;
    private static long lastUpdateTime = 0;
    private static final long CACHE_DURATION_MS = 120000; // 120 seconds

    private BicingClient bicingClient;

    public StationsResource() {
        this.bicingClient = new BicingClient();
    }

    /**
     * GET /ban/stations/list
     * Returns the stations list.
     * Implements 120s caching.
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStations() {
        try {
            long currentTime = System.currentTimeMillis();

            // Logic: If cache is empty OR expired -> Fetch from Bicing API
            if (cachedData == null || (currentTime - lastUpdateTime) > CACHE_DURATION_MS) {
                System.out.println("[StationsResource] Cache expired or empty. Fetching from Bicing API...");
                try {
                    Data data = bicingClient.getBicingStations();

                    if (data != null && data.getData() != null) {
                        cachedData = data;
                        lastUpdateTime = currentTime;
                        System.out.println("[StationsResource] Cache updated at " + new Date(lastUpdateTime));
                    } else {
                        System.err.println("[StationsResource] Received null data from Bicing API");
                    }
                } catch (Exception e) {
                    System.err.println("[StationsResource] Error fetching from API: " + e.getMessage());
                    e.printStackTrace();
                    // If we have old data, maybe we could return it?
                    // But requirement implies strict fetch on expiry.
                    // If fetch fails, we return error if we have no data.
                }
            } else {
                System.out.println("[StationsResource] Returning cached data");
            }

            if (cachedData == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\": \"Unable to fetch data from Bicing\"}")
                        .build();
            }

            return Response.ok(cachedData).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
