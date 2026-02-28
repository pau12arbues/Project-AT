package ban.clients;

import ban.model.AgeAuthResponse;
import ban.model.AgeTokenResponse;
import ban.model.AgeVerifyRequest;
import ban.model.AgeVerifyResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;

public class OpenGatewayClient {

    private static final String CLIENT_ID = "91d2efb5-902e-41e6-9611-19e92824c4fb";
    private static final String CLIENT_SECRET = "60517a55-586c-4645-9341-70cb5717fd5b";

    private static final String BASE_URL = "https://sandbox.opengateway.telefonica.com/apigateway";
    private static final String AUTH_URL = BASE_URL + "/bc-authorize";
    private static final String TOKEN_URL = BASE_URL + "/token";
    private static final String AGE_VERIFY_URL = "https://sandbox.opengateway.telefonica.com/apigateway/kyc-age-verification/v0.1/verify";

    private String getBasicAuthHeader() {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    /**
     * Paso 1: Petición de autorización backend -> obtener auth_req_id
     */
    private String getAuthReqId(String formattedPhone) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(AUTH_URL);

        Form form = new Form();
        form.param("scope", "dpv:FraudPreventionAndDetection kyc-age-verification:verify");
        form.param("login_hint", "tel:" + formattedPhone);

        try {
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", getBasicAuthHeader())
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            if (response.getStatus() == 200) {
                AgeAuthResponse authResponse = response.readEntity(AgeAuthResponse.class);
                return authResponse.getAuth_req_id();
            } else {
                String errorBody = response.readEntity(String.class);
                throw new Exception("Error en bc-authorize. Status: " + response.getStatus() + ", Body: " + errorBody);
            }
        } finally {
            client.close();
        }
    }

    /**
     * Paso 2: Petición de token -> obtener access_token
     */
    private String getAccessToken(String authReqId) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(TOKEN_URL);

        Form form = new Form();
        form.param("grant_type", "urn:openid:params:grant-type:ciba");
        form.param("auth_req_id", authReqId);

        try {
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", getBasicAuthHeader())
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            if (response.getStatus() == 200) {
                AgeTokenResponse tokenResponse = response.readEntity(AgeTokenResponse.class);
                return tokenResponse.getAccess_token();
            } else {
                String errorBody = response.readEntity(String.class);
                throw new Exception(
                        "Error en token endpoint. Status: " + response.getStatus() + ", Body: " + errorBody);
            }
        } finally {
            client.close();
        }
    }

    /**
     * Paso 3: Llamada a la API de Age Verification -> obtener ageOver
     */
    private boolean verifyAge(String accessToken, String formattedPhone) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(AGE_VERIFY_URL);

        // Crear cuerpo de la petición (JSON) con la edad a verificar
        AgeVerifyRequest requestBody = new AgeVerifyRequest(23);

        try {
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Bearer " + accessToken)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE));

            if (response.getStatus() == 200) {
                String rawJson = response.readEntity(String.class);
                System.out.println("[OpenGatewayClient] Raw verify response: " + rawJson);

                // Parse it manually since we already consumed the stream
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                AgeVerifyResponse verifyResponse = mapper.readValue(rawJson, AgeVerifyResponse.class);
                return verifyResponse.isAgeOver();
            } else {
                String errorBody = response.readEntity(String.class);
                throw new Exception(
                        "Error en age-verification API. Status: " + response.getStatus() + ", Body: " + errorBody);
            }
        } finally {
            client.close();
        }
    }

    /**
     * Método público de alto nivel que orquesta los 3 pasos del flujo CIBA.
     */
    public boolean isAgeVerified(String phoneNumber) throws Exception {
        System.out.println("[OpenGatewayClient] Iniciando validación de edad para: " + phoneNumber);

        // Asegurar que el número empieza por +
        String formattedPhone = phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;

        // Paso 1
        String authReqId = getAuthReqId(formattedPhone);
        System.out.println("[OpenGatewayClient] Paso 1 OK. auth_req_id obtenido.");

        // Paso 2
        String accessToken = getAccessToken(authReqId);
        System.out.println("[OpenGatewayClient] Paso 2 OK. access_token obtenido.");

        // Paso 3
        boolean isOver18 = verifyAge(accessToken, formattedPhone);
        System.out.println("[OpenGatewayClient] Paso 3 OK. ageOver: " + isOver18);

        return isOver18;
    }

    public static void main(String[] args) {
        OpenGatewayClient client = new OpenGatewayClient();
        try {
            boolean resultEven = client.isAgeVerified("+34600111222");
            System.out.println("Result Even: " + resultEven);
            boolean resultOdd = client.isAgeVerified("+34600111223");
            System.out.println("Result Odd: " + resultOdd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
