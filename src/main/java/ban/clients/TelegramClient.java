package ban.clients;

import ban.model.Message;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TelegramClient {

    private static final String TELEGRAM_API_URL = "https://api.telegram.org";

    /**
     * Envía un mensaje a través del bot de Telegram.
     *
     * @param token   El bot token para la API de Telegram.
     * @param message El objeto Message que contiene el chat_id y el text.
     * @return true si el mensaje se envió correctamente, false en caso contrario.
     */
    public boolean sendMessage(String token, Message message) {
        Client client = ClientBuilder.newClient();

        // Petición POST a https://api.telegram.org/bot{TOKEN}/sendMessage
        WebTarget target = client.target(TELEGRAM_API_URL)
                .path("/bot" + token + "/sendMessage");

        try {
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(message, MediaType.APPLICATION_JSON_TYPE));

            if (response.getStatus() == 200) {
                return true;
            } else {
                System.err.println("Error al enviar el mensaje de Telegram. Código de estado: " + response.getStatus());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Excepción al intentar enviar el mensaje de Telegram: " + e.getMessage());
            return false;
        } finally {
            client.close();
        }
    }
}
