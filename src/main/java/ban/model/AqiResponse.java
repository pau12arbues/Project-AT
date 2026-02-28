package ban.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AqiResponse {

    private String status;
    private AqiData data;

    public AqiResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AqiData getData() {
        return data;
    }

    public void setData(AqiData data) {
        this.data = data;
    }
}
