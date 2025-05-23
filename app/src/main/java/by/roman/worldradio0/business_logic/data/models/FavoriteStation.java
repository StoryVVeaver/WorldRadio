package by.roman.worldradio0.business_logic.data.models;

public class FavoriteStation {
    private final int id;
    private final int userId;
    private final String stationUUID;

    public FavoriteStation(int id, int userId, String stationUUID) {
        this.id = id;
        this.userId = userId;
        this.stationUUID = stationUUID;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getStationUUID() { return stationUUID; }
}
