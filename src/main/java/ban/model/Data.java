package ban.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    private Stations data;

    public Data() {
    }

    public Data(Stations data) {
        this.data = data;
    }

    public Stations getData() {
        return data;
    }

    public void setData(Stations data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Data{data=" + data + "}";
    }
}
