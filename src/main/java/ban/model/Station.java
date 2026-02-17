package ban.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {
    @JsonProperty("station_id")
    private int station_id;

    @JsonProperty("num_bikes_available")
    private int num_bikes_available;

    @JsonProperty("num_docks_available")
    private int num_docks_available;

    @JsonProperty("last_reported")
    private long last_reported;

    @JsonProperty("is_charging_station")
    private boolean is_charging_station;

    private String status;

    // Extra fields from my previous impl, user didn't explicitly forbid but didn't
    // list.
    // I'll keep them as they are useful for the map/frontend potentially,
    // but the user's snippet didn't have them. I will keep them for now but use
    // their naming convention.
    private double lat;
    private double lon;

    public Station() {
    }

    public Station(int station_id, int num_bikes_available, int num_docks_available, long last_reported,
            boolean is_charging_station, String status) {
        this.station_id = station_id;
        this.num_bikes_available = num_bikes_available;
        this.num_docks_available = num_docks_available;
        this.last_reported = last_reported;
        this.is_charging_station = is_charging_station;
        this.status = status;
    }

    public int getStation_id() {
        return station_id;
    }

    public void setStation_id(int station_id) {
        this.station_id = station_id;
    }

    public int getNum_bikes_available() {
        return num_bikes_available;
    }

    public void setNum_bikes_available(int num_bikes_available) {
        this.num_bikes_available = num_bikes_available;
    }

    public int getNum_docks_available() {
        return num_docks_available;
    }

    public void setNum_docks_available(int num_docks_available) {
        this.num_docks_available = num_docks_available;
    }

    public long getLast_reported() {
        return last_reported;
    }

    public void setLast_reported(long last_reported) {
        this.last_reported = last_reported;
    }

    public boolean isIs_charging_station() {
        return is_charging_station;
    }

    public void setIs_charging_station(boolean is_charging_station) {
        this.is_charging_station = is_charging_station;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Station{id=" + station_id +
                ", bikes=" + num_bikes_available +
                ", docks=" + num_docks_available +
                ", status=" + status + "}";
    }
}
