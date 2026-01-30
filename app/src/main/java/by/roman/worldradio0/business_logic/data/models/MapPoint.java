package by.roman.worldradio0.business_logic.data.models;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;

public class MapPoint {
    private double latitude;
    private double longitude;
    private String uuid;
    public String url;
    private String name;
    private String favicon;
    private String homepage;

    public MapPoint(double latitude, double longitude, String uuid, String url, String name, String favicon, String homepage) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.uuid = uuid;
        this.url = url;
        this.name = name;
        this.favicon = favicon;
        this.homepage = homepage;
    }
    public MapPoint(){};
    public static MapPoint createFrom(RadioStation station) {
        MapPoint mp = new MapPoint();
        mp.latitude = station.getGeoLat();
        mp.longitude = station.getGeoLong();
        mp.uuid = station.getStationUuid();
        mp.url = station.getUrl();
        mp.favicon = station.getFavicon();
        mp.name = station.getName();
        mp.homepage = station.getHomepage();
        return mp;
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
    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
    public String getFavicon() {
        return favicon;
    }
    public String getHomepage() {
        return homepage;
    }
}

