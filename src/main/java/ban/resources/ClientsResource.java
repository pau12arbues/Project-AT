package ban.resources;

import ban.model.Client;
import ban.services.ClientStorage;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/clients")
public class ClientsResource {

    private ClientStorage storage = new ClientStorage();

    /**
     * POST /ban/clients/subscribe
     * Suscribe un cliente a estaciones (SIN verificar edad en demo intermedia)
     */
    @POST
    @Path("/subscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeClient(Client client) {
        try {
            // Validaciones básicas
            if (client.getPhoneNumber() == null || client.getPhoneNumber().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Phone number is required\"}")
                        .build();
            }

            if (client.getStationsIds() == null || client.getStationsIds().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"At least one station ID is required\"}")
                        .build();
            }

            // Guardar cliente (sin verificar edad por ahora)
            storage.addClient(client);

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
     * Devuelve la lista de todos los clientes suscritos
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClients() {
        try {
            List<Client> clients = storage.getAllClients();
            return Response.ok(clients).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
