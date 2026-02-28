package ban.resources;

import ban.model.Client;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ban.clients.BicingClient;
import ban.clients.OpenGatewayClient;
import ban.clients.TelegramClient;
import ban.model.Data;
import ban.model.Message;
import ban.model.Station;

@Path("/clients")
public class ClientsResource {

    // Helper static list to simulate storage
    private static List<Client> clients = new ArrayList<>();

    /** Permite a otros Resources acceder a la lista de clientes. */
    public static List<Client> getClientsList() {
        return clients;
    }

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

            // 3.5 Validate Age using Open Gateway API
            OpenGatewayClient openGatewayClient = new OpenGatewayClient();
            boolean isOver18 = false;
            try {
                isOver18 = openGatewayClient.isAgeVerified(client.getPhoneNumber());
            } catch (Exception ex) {
                System.out.println("[ClientsResource] OpenGateway API Exception: " + ex.getMessage());
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"Age verification error: " + ex.getMessage().replace("\"", "\\\"")
                                + "\"}")
                        .build();
            }

            if (!isOver18) {
                System.out.println("[ClientsResource] Age verification failed for: " + client.getPhoneNumber());
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"User must be 23 or older\"}")
                        .build();
            }

            // Add to list
            synchronized (clients) {
                clients.add(client);
            }

            System.out.println("[ClientsResource] Subscribed: " + client.getPhoneNumber());

            // 4. Fetch Bicing Data and Send Telegram Message
            try {
                BicingClient bicingClient = new BicingClient();
                Data bicingData = bicingClient.getBicingStations();
                List<Station> allStations = bicingData.getData().getStations();

                // Construct the summary message
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder
                        .append("¡Hola! Te has suscrito correctamente.\nAquí tienes el estado de tus estaciones:\n\n");

                boolean foundStations = false;
                for (Integer reqStationId : client.getStationsIds()) {
                    for (Station station : allStations) {
                        if (station.getStation_id() == reqStationId) {
                            foundStations = true;
                            messageBuilder.append("🚴 Estación ").append(reqStationId).append(":\n")
                                    .append("Bicis libres: ").append(station.getNum_bikes_available()).append("\n")
                                    .append("Anclajes libres: ").append(station.getNum_docks_available())
                                    .append("\n\n");
                            break;
                        }
                    }
                }

                if (!foundStations) {
                    messageBuilder
                            .append("Lo sentimos, no hemos podido encontrar datos para las estaciones solicitadas.");
                }

                // Send Telegram Message
                TelegramClient telegramClient = new TelegramClient();
                Message telegramMessage = new Message(String.valueOf(client.getChatId()), messageBuilder.toString());
                boolean sent = telegramClient.sendMessage(client.getTelegramToken(), telegramMessage);

                if (!sent) {
                    System.err.println("[ClientsResource] No se pudo enviar el mensaje a Telegram para el cliente "
                            + client.getPhoneNumber());
                }
            } catch (Exception ex) {
                System.err.println(
                        "[ClientsResource] Error al obtener datos de Bicing o enviar Telegram: " + ex.getMessage());
                // We shouldn't fail the subscription if the notification fails, so we just log
                // the error
            }

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
