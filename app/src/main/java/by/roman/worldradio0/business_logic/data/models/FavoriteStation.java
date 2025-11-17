package by.roman.worldradio0.business_logic.data.models;

public class FavoriteStation {
    private final int id;
    private final int userId;
    private final String uuid;

    public FavoriteStation(int id, int userId, String uuid) {
        this.id = id;
        this.userId = userId;
        this.uuid = uuid;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUUID() { return uuid; }
}
