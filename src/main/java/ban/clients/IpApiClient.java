package ban.clients;

import ban.model.IpApiResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class IpApiClient {

    // Importante: usar HTTP, no HTTPS (la versión gratuita no soporta SSL)
    private static final String BASE_URL = "http://ip-api.com/json/";

    /**
     * Obtiene el nombre de la ciudad a partir de una IP pública.
     * 
     * @param ip dirección IP pública (ej: "83.44.123.45")
     * @return nombre de la ciudad (ej: "Barcelona")
     * @throws Exception si la IP no se puede resolver o la API falla
     */
    public String getCityFromIp(String ip) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(BASE_URL + ip);

        try {
            IpApiResponse response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .get(IpApiResponse.class);

            System.out.println("[IpApiClient] Response: " + response);

            if (!"success".equals(response.getStatus())) {
                throw new Exception("No se pudo resolver la IP: " + ip + ". Status: " + response.getStatus());
            }

            return response.getCity();
        } finally {
            client.close();
        }
    }
}
