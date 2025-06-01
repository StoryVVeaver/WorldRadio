package by.roman.worldradio0.business_logic.data.models;

public class FavoriteTrack {
    private final int id;
    private final int userId;
    private final String track;
    public FavoriteTrack(int id, int userId,String track){
        this.id = id;
        this.userId = userId;
        this.track = track;
    }

    public int getId() {
        return id;
    }
    public int getUserId() {
        return userId;
    }
    public String getTrack() {
        return track;
    }
}
