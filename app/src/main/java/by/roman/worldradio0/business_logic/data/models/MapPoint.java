package by.roman.worldradio0.business_logic.data.models;

public class MapPoint {
    private double latitude;
    private double longitude;
    private String uuid;

    public MapPoint(double latitude, double longitude, String uuid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.uuid = uuid;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getUuid() {
        return uuid;
    }
}

