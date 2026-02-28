package ban.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgeVerifyResponse {

    // The sandbox returns "ageCheck" as a String "true"/"false"
    @JsonProperty("ageCheck")
    private String ageCheck;

    public AgeVerifyResponse() {
    }

    public String getAgeCheck() {
        return ageCheck;
    }

    public void setAgeCheck(String ageCheck) {
        this.ageCheck = ageCheck;
    }

    /** Returns true if the API confirmed the user meets the age threshold. */
    public boolean isAgeOver() {
        return "true".equalsIgnoreCase(ageCheck);
    }
}
