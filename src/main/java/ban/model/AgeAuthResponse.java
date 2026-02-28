package ban.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgeAuthResponse {

    @JsonProperty("auth_req_id")
    private String auth_req_id;

    public AgeAuthResponse() {
    }

    public AgeAuthResponse(String auth_req_id) {
        this.auth_req_id = auth_req_id;
    }

    public String getAuth_req_id() {
        return auth_req_id;
    }

    public void setAuth_req_id(String auth_req_id) {
        this.auth_req_id = auth_req_id;
    }
}
