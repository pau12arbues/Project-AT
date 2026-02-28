package ban.resources;

import ban.clients.AqiClient;
import ban.clients.BicingClient;
import ban.clients.IpApiClient;
import ban.clients.TelegramClient;
import ban.model.Client;
import ban.model.Data;
import ban.model.Message;
import ban.model.Station;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/notifier")
public class NotifierResource {

    /**
     * POST /ban/notifier/slots
     * Recibe el phoneNumber del usuario y envía un mensaje de Telegram
     * con los free slots de las estaciones a las que está suscrito.
     */
    @POST
    @Path("/slots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifySlots(NotifyRequest request) {
        try {
            String phoneNumber = request.getPhoneNumber();
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Phone number is required\"}")
                        .build();
            }

            // Buscar el cliente por teléfono
            Client client = findClientByPhone(phoneNumber);
            if (client == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Client not found with phone: " + phoneNumber + "\"}")
                        .build();
            }

            // Obtener datos de Bicing
            BicingClient bicingClient = new BicingClient();
            Data bicingData = bicingClient.getBicingStations();
            List<Station> allStations = bicingData.getData().getStations();

            // Construir mensaje
            StringBuilder msg = new StringBuilder();
            msg.append("🚲 Estado de tus estaciones Bicing:\n\n");

            boolean found = false;
            for (Integer stationId : client.getStationsIds()) {
                for (Station station : allStations) {
                    if (station.getStation_id() == stationId) {
                        found = true;
                        msg.append("🚴 Estación ").append(stationId).append(":\n")
                                .append("  Bicis libres: ").append(station.getNum_bikes_available()).append("\n")
                                .append("  Anclajes libres: ").append(station.getNum_docks_available())
                                .append("\n\n");
                        break;
                    }
                }
            }

            if (!found) {
                msg.append("No se encontraron datos para tus estaciones.");
            }

            // Enviar por Telegram
            TelegramClient telegramClient = new TelegramClient();
            Message telegramMessage = new Message(String.valueOf(client.getChatId()), msg.toString());
            boolean sent = telegramClient.sendMessage(client.getTelegramToken(), telegramMessage);

            if (sent) {
                System.out.println("[NotifierResource] Slots notification sent to: " + phoneNumber);
                return Response.ok("{\"message\": \"Slots notification sent successfully\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"Failed to send Telegram message\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * POST /ban/notifier/airquality
     * Recibe la IP del dispositivo y el phoneNumber del usuario.
     * Resuelve IP → ciudad → AQI y envía un mensaje de Telegram.
     */
    @POST
    @Path("/airquality")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifyAirQuality(NotifyAirQualityRequest request) {
        try {
            String phoneNumber = request.getPhoneNumber();
            String ip = request.getIp();

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Phone number is required\"}")
                        .build();
            }
            if (ip == null || ip.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"IP address is required\"}")
                        .build();
            }

            // Buscar el cliente
            Client client = findClientByPhone(phoneNumber);
            if (client == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Client not found with phone: " + phoneNumber + "\"}")
                        .build();
            }

            // Paso 1: IP → Ciudad
            IpApiClient ipApiClient = new IpApiClient();
            String city = ipApiClient.getCityFromIp(ip);
            System.out.println("[NotifierResource] IP " + ip + " → City: " + city);

            // Paso 2: Ciudad → AQI
            AqiClient aqiClient = new AqiClient();
            int aqi = aqiClient.getAqiByCity(city);
            String level = AqiClient.getAqiLevel(aqi);
            System.out.println("[NotifierResource] City " + city + " → AQI: " + aqi + " (" + level + ")");

            // Paso 3: Construir mensaje
            StringBuilder msg = new StringBuilder();
            msg.append("🌍 Air Quality Report\n\n")
                    .append("📍 Ciudad: ").append(city).append("\n")
                    .append("📊 AQI: ").append(aqi).append("\n")
                    .append("📋 Nivel: ").append(level).append("\n\n");

            // Añadir recomendación según nivel
            if (aqi <= 50) {
                msg.append("✅ La calidad del aire es buena. ¡Disfruta del exterior!");
            } else if (aqi <= 100) {
                msg.append("⚠️ Calidad moderada. Aceptable para la mayoría.");
            } else if (aqi <= 150) {
                msg.append("🟠 Poco saludable para grupos sensibles.");
            } else if (aqi <= 200) {
                msg.append("🔴 Poco saludable. Evita actividades prolongadas al aire libre.");
            } else if (aqi <= 300) {
                msg.append("🟣 Muy poco saludable. Limita la exposición al exterior.");
            } else {
                msg.append("⛔ Peligroso. Evita salir al exterior.");
            }

            // Paso 4: Enviar por Telegram
            TelegramClient telegramClient = new TelegramClient();
            Message telegramMessage = new Message(String.valueOf(client.getChatId()), msg.toString());
            boolean sent = telegramClient.sendMessage(client.getTelegramToken(), telegramMessage);

            if (sent) {
                System.out.println("[NotifierResource] Air quality notification sent to: " + phoneNumber);
                return Response.ok("{\"message\": \"Air quality notification sent successfully\", " +
                        "\"city\": \"" + city + "\", \"aqi\": " + aqi + ", \"level\": \"" + level + "\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"Failed to send Telegram message\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /** Busca un cliente por su número de teléfono. */
    private Client findClientByPhone(String phoneNumber) {
        for (Client c : ClientsResource.getClientsList()) {
            if (phoneNumber.equals(c.getPhoneNumber())) {
                return c;
            }
        }
        return null;
    }

    // ── Inner classes para los request bodies ──

    public static class NotifyRequest {
        private String phoneNumber;

        public NotifyRequest() {
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public static class NotifyAirQualityRequest {
        private String phoneNumber;
        private String ip;

        public NotifyAirQualityRequest() {
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }
}
