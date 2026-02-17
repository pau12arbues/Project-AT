package ban.clients;

import ban.model.Data;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

public class BicingClient {

    private static final String BICING_URL = "https://opendata-ajuntament.barcelona.cat";
    private static final String BICING_PATH = "/data/dataset/6aa3416d-ce1a-494d-861b-7bd07f069600/resource/1b215493-9e63-4a12-8980-2d7e0fa19f85/download";
    private static final String BICING_TOKEN = "068d30c4b25c0accf06d2ca06f37843592efe23f036bd5f35037c972bc6fb8c7";

    /**
     * Obtiene los datos actuales de Bicing desde la API oficial
     */
    public Data getBicingStations() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(BICING_URL).path(BICING_PATH);

        try {
            Data data = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", BICING_TOKEN)
                    .get(new GenericType<Data>() {
                    });
            return data;
        } finally {
            client.close();
        }
    }
}
