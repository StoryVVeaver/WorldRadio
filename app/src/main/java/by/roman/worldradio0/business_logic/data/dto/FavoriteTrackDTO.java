package by.roman.worldradio0.business_logic.data.dto;

import by.roman.worldradio0.business_logic.data.models.FavoriteTrack;

public class FavoriteTrackDTO {
    public int id;
    public int userId;
    public String track;
    public FavoriteTrack toModel(){
        return new FavoriteTrack(id,userId,track);
    }
    public FavoriteTrackDTO fromModel(FavoriteTrack favoriteTrack){
        FavoriteTrackDTO dto = new FavoriteTrackDTO();
        dto.id = favoriteTrack.getId();
        dto.userId = favoriteTrack.getUserId();
        dto.track = favoriteTrack.getTrack();
        return dto;
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
