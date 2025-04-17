package by.roman.worldradio0.business_logic.data.models;

public class FavoriteStation {
    private final int userId;
    private final String stationUUID;

    public FavoriteStation(int userId, String stationUUID) {
        this.userId = userId;
        this.stationUUID = stationUUID;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getStationUUID() { return stationUUID; }
}
