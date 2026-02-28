package ban.clients;

import ban.model.AqiResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class AqiClient {

    private static final String TOKEN = "645c0e9b9a951d3d57c3806d2848728515d6dc74";
    private static final String BASE_URL = "https://api.waqi.info/feed/";

    /**
     * Obtiene el Air Quality Index (AQI) de una ciudad.
     *
     * @param city nombre de la ciudad (ej: "Barcelona")
     * @return valor AQI como entero (ej: 42)
     * @throws Exception si la API falla o la ciudad no se encuentra
     */
    public int getAqiByCity(String city) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(BASE_URL + city + "/")
                .queryParam("token", TOKEN);

        try {
            AqiResponse response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .get(AqiResponse.class);

            System.out.println("[AqiClient] City: " + city + ", Status: " + response.getStatus()
                    + ", AQI: " + (response.getData() != null ? response.getData().getAqi() : "null"));

            if (!"ok".equals(response.getStatus())) {
                throw new Exception("No se pudo obtener el AQI para la ciudad: " + city);
            }

            return response.getData().getAqi();
        } finally {
            client.close();
        }
    }

    /**
     * Traduce el valor numérico AQI a un nivel descriptivo.
     * Referencia: https://aqicn.org/scale/
     */
    public static String getAqiLevel(int aqi) {
        if (aqi <= 50)
            return "Good";
        if (aqi <= 100)
            return "Moderate";
        if (aqi <= 150)
            return "Unhealthy for Sensitive Groups";
        if (aqi <= 200)
            return "Unhealthy";
        if (aqi <= 300)
            return "Very Unhealthy";
        return "Hazardous";
    }
}
