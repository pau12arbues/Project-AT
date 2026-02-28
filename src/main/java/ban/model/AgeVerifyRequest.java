package ban.model;

public class AgeVerifyRequest {

    private int ageThreshold;

    public AgeVerifyRequest() {
    }

    public AgeVerifyRequest(int ageThreshold) {
        this.ageThreshold = ageThreshold;
    }

    public int getAgeThreshold() {
        return ageThreshold;
    }

    public void setAgeThreshold(int ageThreshold) {
        this.ageThreshold = ageThreshold;
    }
}
