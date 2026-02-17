package ban.resources;

import ban.model.Client;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/clients")
public class ClientsResource {

    // Helper static list to simulate storage
    private static List<Client> clients = new ArrayList<>();

    /**
     * POST /ban/clients/subscribe
     * Subscribe client to stations.
     */
    @POST
    @Path("/subscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeClient(Client client) {
        try {
            // 1. Validate Phone
            if (client.getPhoneNumber() == null || client.getPhoneNumber().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Phone number is required\"}")
                        .build();
            }

            // 2. Validate Stations
            if (client.getStationsIds() == null || client.getStationsIds().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"At least one station ID is required\"}")
                        .build();
            }

            // 3. Validate Telegram Data (Strict Requirement)
            if (client.getTelegramToken() == null || client.getTelegramToken().isEmpty() || client.getChatId() == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Telegram token and Chat ID are required\"}")
                        .build();
            }

            // Add to list
            synchronized (clients) {
                clients.add(client);
            }

            System.out.println("[ClientsResource] Subscribed: " + client.getPhoneNumber());

            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Client subscribed successfully\", " +
                            "\"phone\": \"" + client.getPhoneNumber() + "\"}")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * GET /ban/clients/list
     * Returns the list of clients.
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClients() {
        try {
            return Response.ok(clients).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
